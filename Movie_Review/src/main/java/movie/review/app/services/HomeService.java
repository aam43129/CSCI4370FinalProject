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

import movie.review.app.services.UtilityService;

/**
 * This service contains home-related functions.
 */
@Service
public class HomeService {

    private final DataSource dataSource;

    @Autowired
    public HomeService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<Movie> getMovies(String userId) {
        final String sql
                = "SELECT m.movie_id movie_id, m.title title, m.poster poster, m.tagline tagline "
                + UtilityService.getListedQuery() // selects isListed as a column
                + "FROM movie m, review r "
                + "WHERE m.movie_id = r.movie_id "
                + "AND m.movie_id = r.movie_id "
                + "GROUP BY m.movie_id "
                + "ORDER BY AVG(r.rating) DESC "
                + "LIMIT 10;";

        List<Movie> movies = new ArrayList<>();

        try (Connection conn = dataSource.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userId); // for the listedQuery 

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String movieId = rs.getString("movie_id");
                    String title = rs.getString("title");
                    String poster = rs.getString("poster");
                    String tagline = rs.getString("tagline");
                    Boolean isListed = rs.getInt("isListed") == 1;

                    movies.add(new Movie(movieId, title, poster, tagline, isListed));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return movies;
    }
}
