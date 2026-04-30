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

    public List<Movie> getMovies(String userId, String genreName) {
        /*final String sql
                = "SELECT m.movie_id movie_id, m.title title, m.poster poster, m.tagline tagline "
                + UtilityService.getListedQuery() // selects isListed as a column
                + "FROM movie m, review r "
                + "WHERE m.movie_id = r.movie_id "
                + "AND m.movie_id = r.movie_id "
                + "GROUP BY m.movie_id "
                + "ORDER BY AVG(r.rating) DESC "
                + "LIMIT 10;";*/
        /*final String sql =
                "SELECT m.movie_id, m.title, " +
                        "IFNULL(m.poster, 'https://via.placeholder.com/500x750?text=No+Poster') as poster, " +
                        "IFNULL(m.tagline, '') as tagline, " +
                        "IF(uml.user_id IS NULL, 0, 1) as isListed " +
                        "FROM Movie m " +
                        "LEFT JOIN user_movie_list uml ON m.movie_id = uml.movie_id AND uml.user_id = ? " +
                        "WHERE m.vote_count > 100 " +
                        "ORDER BY m.popularity DESC " +
                        "LIMIT 12;";*/
        final String sql =
                "SELECT m.movie_id, m.title, " +
                        "IFNULL(m.poster, 'https://via.placeholder.com/500x750?text=No+Poster') as poster, " +
                        "IFNULL(m.tagline, '') as tagline, " +
                        "IF(uml.user_id IS NULL, 0, 1) as isListed " +
                        "FROM Movie m " +
                        "JOIN Movie_Genre mg ON m.movie_id = mg.movie_id " +
                        "JOIN Genre g ON mg.genre_id = g.genre_id " +
                        "LEFT JOIN user_movie_list uml ON m.movie_id = uml.movie_id AND uml.user_id = ? " +
                        "WHERE g.name = ? " +
                        "ORDER BY m.popularity DESC " +
                        "LIMIT 6;";

        List<Movie> movies = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userId); // for the listedQuery 
            pstmt.setString(2, genreName);
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

    // Keep your existing 2-parameter method, and ADD this 1-parameter version:
    public List<Movie> getMovies(String userId) {
        final String sql =
                "SELECT m.movie_id, m.title, " +
                        "IFNULL(m.poster, 'https://via.placeholder.com/500x750?text=No+Poster') as poster, " +
                        "IFNULL(m.tagline, '') as tagline, " +
                        "IF(uml.user_id IS NULL, 0, 1) as isListed " +
                        "FROM Movie m " +
                        "LEFT JOIN user_movie_list uml ON m.movie_id = uml.movie_id AND uml.user_id = ? " +
                        "WHERE m.vote_count > 100 " +
                        "ORDER BY m.popularity DESC " +
                        "LIMIT 14;";

        List<Movie> movies = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    movies.add(new Movie(
                            rs.getString("movie_id"),
                            rs.getString("title"),
                            rs.getString("poster"),
                            rs.getString("tagline"),
                            rs.getInt("isListed") == 1
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return movies;
    }

    public List<String> getTopGenresForUser(String userId) {
        List<String> genres = new ArrayList<>();
        String sql =
                "SELECT g.name FROM Genre g " +
                        "JOIN Movie_Genre mg ON g.genre_id = mg.genre_id " +
                        "LEFT JOIN Review r ON mg.movie_id = r.movie_id AND r.user_id = ? " +
                        "GROUP BY g.genre_id " +
                        "ORDER BY IFNULL(AVG(r.rating), 0) DESC, COUNT(mg.movie_id) DESC " +
                        "LIMIT 3;";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) { genres.add(rs.getString("name")); }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return genres;
    }


}
