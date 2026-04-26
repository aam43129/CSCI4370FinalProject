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

    public static String buildQuery(String userId, String sortBy, String sortByOrder, String minRating, String[] genres, String prodCompany) {
        // building a query to return to the controller
        // for usage with getMovies and getPages
        // ideally this query will not have the select portion in it
        String fromClause = "FROM movie m "; // depends on what you're filtering on
        String filterClause = "WHERE m.popularity > 80 "; // minRating, genres, and prodCompany
        String orderByClause = "ORDER BY m.popularity desc"; // sortBy and sortByOrder
        return fromClause + filterClause + orderByClause;
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

        try (Connection conn = dataSource.getConnection()) {
            // Get the movies
            PreparedStatement pstmt1 = conn.prepareStatement(query1);
            pstmt1.setString(1, currentUserId); // for the isListedQuery

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
            // Get the total count
            PreparedStatement pstmt2 = conn.prepareStatement(query2);
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
