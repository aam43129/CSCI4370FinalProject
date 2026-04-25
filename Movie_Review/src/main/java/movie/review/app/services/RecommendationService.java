package movie.review.app.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;

import movie.review.app.models.Movie;
import movie.review.app.models.Page;

public class RecommendationService {
    
    private final DataSource dataSource;

    @Autowired
    public RecommendationService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<Movie> getSortedAndFilteredMovies(String sortBy, String sortByOrder, String minRating, String[] genres, String prodCompany, int offset) {
        List<Movie> movies = new ArrayList<>();

        // sortby
        // asc or desc
        // genre[]        

        
        final String sql = "";
        

        try (Connection conn = dataSource.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            try (ResultSet rs = pstmt.executeQuery()) {

                while (rs.next()) {
                    String movieId = rs.getString("movieId");
                    String title = rs.getString("title");
                    String poster = rs.getString("poster");

                    movies.add(new Movie(movieId, title, poster));
                }
            }
        } catch (SQLException e) {
            System.out.println(sql);
            e.printStackTrace();
        }
        return movies;
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
