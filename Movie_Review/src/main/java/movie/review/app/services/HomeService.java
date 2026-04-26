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

    public List<Movie> getMovies() {
        final String sql
                = "SELECT m.movie_Id movieId, m.title title, m.poster poster "
                + "FROM movie m, review r "
                + "WHERE m.movie_id = r.movie_id "
                + "GROUP BY m.movie_id "
                + "ORDER BY AVG(r.rating) DESC "
                + "LIMIT 10;";

        List<Movie> movies = new ArrayList<>();

        try (Connection conn = dataSource.getConnection(); 
        PreparedStatement pstmt = conn.prepareStatement(sql)) {

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String movieId = rs.getString("movieId");
                    String title = rs.getString("title");
                    String poster = rs.getString("poster");

                    movies.add(new Movie(movieId, title, poster));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return movies;
    }
}
