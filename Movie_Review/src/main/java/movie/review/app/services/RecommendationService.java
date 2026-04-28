package movie.review.app.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import movie.review.app.models.Movie;
import movie.review.app.models.RecommendationResponse;

@Service
public class RecommendationService {

    private final DataSource dataSource;

    @Autowired
    public RecommendationService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public static String buildQuery(String userId, String sortBy, String sortByOrder, String minRating, String[] genres,
            String prodCompany) {

        StringBuilder from = new StringBuilder("FROM movie m ");
        StringBuilder where = new StringBuilder("WHERE 1=1 ");

        // MINIMUM RATING filter - average of user reviews
        if (minRating != null && !minRating.isEmpty()) {
            where.append("AND m.movie_id IN (")
                    .append("SELECT r.movie_id FROM review r ")
                    .append("GROUP BY r.movie_id ")
                    .append("HAVING AVG(r.rating) >= ").append(minRating)
                    .append(") ");
        }

        // GENRE filter
        if (genres != null && genres.length > 0) {
            where.append("AND m.movie_id IN (")
                    .append("SELECT mg.movie_id FROM movie_genre mg ")
                    .append("JOIN genre g ON mg.genre_id = g.genre_id ")
                    .append("WHERE g.name IN (");

            for (int i = 0; i < genres.length; i++) {
                String dbGenreName = mapGenreName(genres[i]);
                where.append("'").append(dbGenreName).append("'");
                if (i < genres.length - 1)
                    where.append(", ");
            }
            where.append(")) ");
        }

        // PRODUCTION COMPANY filter
        if (prodCompany != null && !prodCompany.isEmpty()) {
            where.append("AND m.movie_id IN (")
                    .append("SELECT mc.movie_id FROM movie_company mc ")
                    .append("JOIN production_company pc ON mc.company_id = pc.company_id ")
                    .append("WHERE pc.name LIKE '%").append(prodCompany).append("%'")
                    .append(") ");
        }

        // SORT
        String orderBy;
        if (sortBy == null || sortBy.isEmpty()) {
            orderBy = "ORDER BY m.popularity DESC ";
        } else {
            String order = (sortByOrder != null && sortByOrder.equalsIgnoreCase("asc")) ? "ASC" : "DESC";
            String orderByColumn;
            switch (sortBy) {
                case "popularity":
                    orderByColumn = "m.popularity";
                    break;
                case "runtime":
                    orderByColumn = "m.runtime";
                    break;
                case "rating":
                    orderByColumn = "m.vote_average";
                    break;
                case "revenue":
                    orderByColumn = "m.revenue";
                    break;
                case "releaseDate":
                    orderByColumn = "m.release_date";
                    break;
                default:
                    orderByColumn = "m.popularity";
                    break;
            }
            orderBy = "ORDER BY " + orderByColumn + " " + order + " ";
        }

        System.out.println("DEBUG QUERY: " + from.toString() + where.toString() + orderBy);
        return from.toString() + where.toString() + orderBy;
    }

// Maps frontend checkbox values to database genre names
private static String mapGenreName(String value) {
    switch (value) {
        case "action":        return "Action";
        case "adventure":     return "Adventure";
        case "animation":     return "Animation";
        case "comedy":        return "Comedy";
        case "crime":         return "Crime";
        case "documentary":   return "Documentary";
        case "drama":         return "Drama";
        case "family":        return "Family";
        case "fantasy":       return "Fantasy";
        case "history":       return "History";
        case "horror":        return "Horror";
        case "music":         return "Music";
        case "mystery":       return "Mystery";
        case "romance":       return "Romance";
        case "sci-fi":        return "Science Fiction";
        case "thriller":      return "Thriller";
        case "tv-movie":      return "TV Movie";
        case "war":           return "War";
        case "western":       return "Western";
        default:              return value;
    }
}

    public RecommendationResponse getMovies(String currentUserId, int pageNum, String queryFragment) {
        List<Movie> movies = new ArrayList<>();
        int totalCount = 0; // numResults, not numPages

        // 1. Build queries without internal semicolons
        String query1 = "select m.movie_id movie_id, m.title title, m.poster poster, m.tagline tagline "
                + UtilityService.getListedQuery() 
                + queryFragment;
        String query2 = "select count(*) " + queryFragment;

        // 2. Limit/Offset go at the VERY end
        int offset = (pageNum - 1) * 20;
        query1 += " LIMIT 20 OFFSET " + offset;

        try (Connection conn = dataSource.getConnection();
                PreparedStatement pstmt1 = conn.prepareStatement(query1);
                PreparedStatement pstmt2 = conn.prepareStatement(query2)) {

            pstmt1.setString(1, currentUserId);

            try (ResultSet rs = pstmt1.executeQuery()) {
                while (rs.next()) {
                    String movieId = rs.getString("movie_id");
                    String title = rs.getString("title");
                    String poster = rs.getString("poster");
                    String tagline = rs.getString("tagline");
                    Boolean isListed = rs.getInt("isListed") == 1;
                    movies.add(new Movie(movieId, title, poster, tagline, isListed));
                }
            }

            try (ResultSet rs = pstmt2.executeQuery()) {
                if (rs.next()) {
                    totalCount = rs.getInt(1);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        // Return totalCount so getPages can calculate totalPages
        return new RecommendationResponse(movies, totalCount);
    }

}
