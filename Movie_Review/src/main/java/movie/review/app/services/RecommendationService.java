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
import movie.review.app.models.Page;
import movie.review.app.models.RecommendationResponse;

@Service
public class RecommendationService {

    private final DataSource dataSource;

    @Autowired
    public RecommendationService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public static String buildQuery(String sortBy, String sortByOrder, String minRating, String[] genres, String prodCompany) {
        // building a query to return to the controller
        // for usage with getMovies and getPages
        // ideally this query will not have the select portion in it
        return "from movie where popularity > 80 order by popularity desc ";
    }

    public RecommendationResponse getMovies(int pageNum, String queryFragment) {
        List<Movie> movies = new ArrayList<>();
        int totalCount = 0; // numResults, not numPages

        // 1. Build queries without internal semicolons
        String query1 = "select movie_id, title, poster " + queryFragment;
        String query2 = "select count(*) " + queryFragment;

        // 2. Limit/Offset go at the VERY end
        int offset = (pageNum - 1) * 20;
        query1 += " LIMIT 20 OFFSET " + offset;

        try (Connection conn = dataSource.getConnection()) {
            // Get the movies
            try (PreparedStatement pstmt = conn.prepareStatement(query1); ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    movies.add(new Movie(rs.getString("movie_id"), rs.getString("title"), rs.getString("poster")));
                }
            }
            // Get the total count
            try (PreparedStatement pstmt = conn.prepareStatement(query2); ResultSet rs = pstmt.executeQuery()) {
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

    /*
    * This method is for returning the pagination.
    * eg: for 8 pages: 1, 2, 3, 4, ... 7, 8
     */
    public static List<Page> getPages(int pageNum, int numResults) {

        int totalPages = (int) Math.ceil(numResults / (double) 20);

        List<Page> pagination = new ArrayList<>();

        for (int i = 1; i <= totalPages; i++) {
            // Only add first 4 pages and the last 2 pages
            if (i <= 4 || i > totalPages - 2) {
                pagination.add(new Page(i, i == pageNum));
            } // Add the "..." separator one time after page 4
            else if (i == 5) {
                pagination.add(Page.setSeparator());
            }
        }

        return pagination;
    }
}
