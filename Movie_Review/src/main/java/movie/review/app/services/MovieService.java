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

import movie.review.app.models.DetailedMovie;

/**
 * This service contains home-related functions.
 */
@Service
public class MovieService {

    private final DataSource dataSource;

    @Autowired
    public MovieService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public DetailedMovie getMovies(String currentUserId, String movieId) {
        final String sql
                = "SELECT m.* "
                + UtilityService.getListedQuery()
                + "FROM movie m  "
                + "WHERE m.movie_id = ? ";

        DetailedMovie movie = null;

        try (Connection conn = dataSource.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, currentUserId); // for the isListed query
            pstmt.setString(2, movieId); // for the isListed query

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String title = rs.getString("title");
                    String poster = rs.getString("poster");
                    String tagline = rs.getString("tagline");
                    Boolean isListed = rs.getInt("isListed") == 1;

                    double vote_average = rs.getDouble("vote_average");
                    int vote_count = rs.getInt("vote_count");
                    String release_date = rs.getString("release_date");
                    long revenue = rs.getLong("revenue");
                    long budget = rs.getLong("budget");
                    String homepage = rs.getString("homepage");
                    int runtime = rs.getInt("runtime");
                    String original_language = rs.getString("original_language");
                    String original_title = rs.getString("original_title");
                    String overview = rs.getString("overview");
                    double popularity = rs.getDouble("popularity");

                    movie = new DetailedMovie(movieId, title, poster, tagline, isListed, vote_average, vote_count, release_date, revenue, budget, homepage, runtime, original_language, original_title, overview, popularity);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return movie;
    }
}
