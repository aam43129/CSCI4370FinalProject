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

        if (minRating != null && !minRating.isEmpty()) {
            where.append("AND m.movie_id IN (")
                    .append("SELECT r.movie_id FROM review r ")
                    .append("GROUP BY r.movie_id ")
                    .append("HAVING AVG(r.rating) >= ?) ");
        }

        if (genres != null && genres.length > 0) {
            where.append("AND m.movie_id IN (")
                    .append("SELECT mg.movie_id FROM movie_genre mg ")
                    .append("JOIN genre g ON mg.genre_id = g.genre_id ")
                    .append("WHERE g.name IN (");
            for (int i = 0; i < genres.length; i++) {
                where.append("?");
                if (i < genres.length - 1)
                    where.append(", ");
            }
            where.append(")) ");
        }

        if (prodCompany != null && !prodCompany.isEmpty()) {
            where.append("AND m.movie_id IN (")
                    .append("SELECT mc.movie_id FROM movie_company mc ")
                    .append("JOIN production_company pc ON mc.company_id = pc.company_id ")
                    .append("WHERE pc.name LIKE ?) ");
        }

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

        return from.toString() + where.toString() + orderBy;
    }

    public static List<Object> buildParams(String minRating, String[] genres, String prodCompany) {
        List<Object> params = new ArrayList<>();

        if (minRating != null && !minRating.isEmpty()) {
            params.add(Double.parseDouble(minRating));
        }

        if (genres != null && genres.length > 0) {
            for (String genre : genres) {
                params.add(mapGenreName(genre));
            }
        }

        if (prodCompany != null && !prodCompany.isEmpty()) {
            params.add("%" + prodCompany + "%");
        }

        return params;
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

    public RecommendationResponse getMovies(String currentUserId, int pageNum, String queryFragment, List<Object> filterParams) {
    List<Movie> movies = new ArrayList<>();
    int totalCount = 0;

    String query1 = "select m.movie_id movie_id, m.title title, m.poster poster, m.tagline tagline "
            + UtilityService.getListedQuery()
            + queryFragment;
    String query2 = "select count(*) " + queryFragment;

    int offset = (pageNum - 1) * 20;
    query1 += " LIMIT 20 OFFSET " + offset;

    try (Connection conn = dataSource.getConnection();
         PreparedStatement pstmt1 = conn.prepareStatement(query1);
         PreparedStatement pstmt2 = conn.prepareStatement(query2)) {

        // Set the isListed parameter first
        pstmt1.setString(1, currentUserId);

        // Set filter parameters for query1
        for (int i = 0; i < filterParams.size(); i++) {
            Object param = filterParams.get(i);
            if (param instanceof Double) {
                pstmt1.setDouble(i + 2, (Double) param);
            } else {
                pstmt1.setString(i + 2, (String) param);
            }
        }

        // Set filter parameters for query2
        for (int i = 0; i < filterParams.size(); i++) {
            Object param = filterParams.get(i);
            if (param instanceof Double) {
                pstmt2.setDouble(i + 1, (Double) param);
            } else {
                pstmt2.setString(i + 1, (String) param);
            }
        }

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

    return new RecommendationResponse(movies, totalCount);
}

}
