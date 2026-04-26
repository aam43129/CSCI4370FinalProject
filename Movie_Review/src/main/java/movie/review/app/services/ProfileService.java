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
public class ProfileService {

    private final DataSource dataSource;

    @Autowired
    public ProfileService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<Movie> getMovies(String currentUserId) {
        final String sql
                = "SELECT m.movie_id movie_id, m.title title, m.poster poster, m.tagline tagline "
                + "FROM movie m, user_movie_list uml "
                + "WHERE uml.user_id = ? "
                + "AND m.movie_id = uml.movie_id "
                + "GROUP BY m.movie_id "
                + "ORDER BY AVG(m.popularity) DESC "
                + "LIMIT 10;";

        List<Movie> movies = new ArrayList<>();

        try (Connection conn = dataSource.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, currentUserId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String movieId = rs.getString("movie_id");
                    String title = rs.getString("title");
                    String poster = rs.getString("poster");
                    String tagline = rs.getString("tagline");
                    Boolean isListed = rs.getInt("isListed") == 1;

                    // isListed is true because rs returns user-listed movies
                    movies.add(new Movie(movieId, title, poster, tagline, true)); 
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return movies;
    }
}
