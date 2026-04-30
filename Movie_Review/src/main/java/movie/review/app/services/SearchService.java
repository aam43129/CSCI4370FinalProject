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

@Service
public class SearchService {

    private final DataSource dataSource;

    @Autowired
    public SearchService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<Movie> getMovies(String query, String userId) {
        final String sql =
                "SELECT m.movie_id, m.title, " +
                "IFNULL(m.poster, 'https://via.placeholder.com/500x750?text=No+Poster') AS poster, " +
                "IFNULL(m.tagline, '') AS tagline, " +
                "IF(uml.user_id IS NULL, 0, 1) AS isListed " +
                "FROM Movie m " +
                "LEFT JOIN user_movie_list uml ON m.movie_id = uml.movie_id AND uml.user_id = ? " +
                "WHERE LOWER(m.title) LIKE CONCAT('%', LOWER(?),'%') " +
                "ORDER BY m.popularity DESC " +
                "LIMIT 50;";

        List<Movie> movies = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, userId);
            pstmt.setString(2, query);

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
