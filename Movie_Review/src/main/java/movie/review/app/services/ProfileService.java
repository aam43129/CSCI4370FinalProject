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

import movie.review.app.models.DetailedReview;
import movie.review.app.models.Movie;
import movie.review.app.models.Profile;

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

    public Profile getProfile(String currentUserId, String userId) {
        List<Movie> movies = getMovies(currentUserId, userId);
        List<DetailedReview> reviews = getReviews(currentUserId, userId);
        String userName = reviews != null ? reviews.get(0).getUserName() : "";

        Profile profile = new Profile(userName, movies, reviews, currentUserId.equals(userId));
        return profile;
    }

    public List<Movie> getMovies(String currentUserId, String userId) {
        final String sql
                = "SELECT m.movie_id movie_id, m.title title, m.poster poster, m.tagline tagline "
                + UtilityService.getListedQuery()
                + "FROM movie m, user_movie_list uml "
                + "WHERE uml.user_id = ? "
                + "AND m.movie_id = uml.movie_id "
                + "GROUP BY m.movie_id "
                + "ORDER BY AVG(m.popularity) DESC";

        List<Movie> movies = new ArrayList<>();

        try (Connection conn = dataSource.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, currentUserId); // for isListedQuery
            pstmt.setString(2, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String movieId = rs.getString("movie_id");
                    String title = rs.getString("title");
                    String poster = rs.getString("poster");
                    String tagline = rs.getString("tagline");
                    Boolean isListed = rs.getInt("isListed") == 1;

                    // isListed is true because rs returns user-listed movies
                    movies.add(new Movie(movieId, title, poster, tagline, isListed));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return movies;
    }

    public List<DetailedReview> getReviews(String currentUserId, String userId) {
        Boolean forUser = currentUserId.equals(userId);
        final String movieReviewsQuery
                = "SELECT r.*, DATE_FORMAT(postDate, '%b %d, %Y') as post_date, CONCAT(u.firstName, ' ', u.lastName) as userName, u.user_id user_id, m.title, m.poster, m.tagline "
                + UtilityService.getListedQuery()
                + "FROM review r, user u, movie m "
                + "WHERE r.user_id = u.user_id "
                + "AND r.user_id = ? "
                + "AND m.movie_id = r.movie_id ";

        List<DetailedReview> reviews = null; // initialized to null for mustache logic

        try (Connection conn = dataSource.getConnection()) {
            PreparedStatement pstmtReviews = conn.prepareStatement(movieReviewsQuery);
            pstmtReviews.setString(1, currentUserId); // for isListedQuery
            pstmtReviews.setString(2, userId);
            try (ResultSet reviewsRs = pstmtReviews.executeQuery()) {
                while (reviewsRs.next()) {
                    if (reviews == null) {
                        reviews = new ArrayList<>();
                    }
                    String userName = reviewsRs.getString("userName");
                    String reviewId = reviewsRs.getString("review_id");
                    String content = reviewsRs.getString("content");
                    String postDate = reviewsRs.getString("post_date");
                    Double rating = reviewsRs.getDouble("rating");

                    String movieId = reviewsRs.getString("movie_id");
                    String title = reviewsRs.getString("title");
                    String poster = reviewsRs.getString("poster");
                    String tagline = reviewsRs.getString("tagline");
                    Boolean isListed = reviewsRs.getInt("isListed") == 1;

                    Movie reviewMovie = new Movie(movieId, title, poster, tagline, isListed);
                    DetailedReview review = new DetailedReview(userName, userId, reviewId, movieId, content, postDate, rating, forUser, reviewMovie);
                    reviews.add(review);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reviews;
    }
}
