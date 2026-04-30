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
import movie.review.app.models.Review;

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
        final String movieInfoQuery = "SELECT m.*, AVG(r.rating) avg_rating "
                + UtilityService.getListedQuery()
                + "FROM movie m "
                + "LEFT JOIN review r ON m.movie_id = r.movie_id "
                + "WHERE m.movie_id = ? "
                + "GROUP BY m.movie_id";

        DetailedMovie movie = null;

        try (Connection conn = dataSource.getConnection()) {
            List<String> genres = getGenres(movieId);
            List<String> companies = getCompanies(movieId);
            List<Review> reviewMadeByUser = getReviews(movieId, currentUserId, true);
            List<Review> reviewsMadeByOthers = getReviews(movieId, currentUserId, false);

            try (PreparedStatement pstmtMovieInfo = conn.prepareStatement(movieInfoQuery)) {
                pstmtMovieInfo.setString(1, currentUserId);
                pstmtMovieInfo.setString(2, movieId);

                try (ResultSet movieRs = pstmtMovieInfo.executeQuery()) {
                    if (movieRs.next()) {
                        String title = movieRs.getString("title");
                        String poster = movieRs.getString("poster");
                        String tagline = movieRs.getString("tagline");
                        Boolean isListed = movieRs.getInt("isListed") == 1;
                        double vote_average = movieRs.getDouble("vote_average");
                        int vote_count = movieRs.getInt("vote_count");
                        String release_date = movieRs.getString("release_date");
                        long revenue = movieRs.getLong("revenue");
                        long budget = movieRs.getLong("budget");
                        String homepage = movieRs.getString("homepage");
                        int runtime = movieRs.getInt("runtime");
                        String original_language = movieRs.getString("original_language");
                        String original_title = movieRs.getString("original_title");
                        String overview = movieRs.getString("overview");
                        double popularity = movieRs.getDouble("popularity");
                        double avg_rating = movieRs.getDouble("avg_rating");

                        movie = new DetailedMovie(movieId, title, poster, tagline, isListed, vote_average, vote_count,
                                release_date, revenue, budget, homepage, runtime, original_language, original_title,
                                overview, popularity, avg_rating, genres, companies, reviewMadeByUser,
                                reviewsMadeByOthers);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return movie;
    }

    public List<String> getGenres(String movieId) throws SQLException {
        final String movieGenresQuery = "SELECT g.name name "
                + "FROM genre g, movie_genre mg "
                + "WHERE mg.movie_id = ? "
                + "AND g.genre_id = mg.genre_id";

        List<String> genres = new ArrayList<>();

        try (Connection conn = dataSource.getConnection(); PreparedStatement pstmtGenres = conn.prepareStatement(movieGenresQuery)) {
            pstmtGenres.setString(1, movieId);
            try (ResultSet genresRs = pstmtGenres.executeQuery()) {
                while (genresRs.next()) {
                    genres.add(genresRs.getString("name"));
                }
            }
        }
        return genres;
    }

    public List<String> getCompanies(String movieId) throws SQLException {
        final String movieCompaniesQuery = "SELECT pc.name name "
                + "FROM production_company pc, movie_company mc "
                + "WHERE mc.movie_id = ? "
                + "AND mc.company_id = pc.company_id";

        List<String> companies = new ArrayList<>();

        try (Connection conn = dataSource.getConnection(); PreparedStatement pstmtCompanies = conn.prepareStatement(movieCompaniesQuery)) {
            pstmtCompanies.setString(1, movieId);
            try (ResultSet companyRs = pstmtCompanies.executeQuery()) {
                while (companyRs.next()) {
                    companies.add(companyRs.getString("name"));
                }
            }
        }
        return companies;
    }

    public List<Review> getReviews(String movieId, String currentUserId, Boolean forUser) throws SQLException {
        String operator = forUser ? "=" : "!=";
        final String movieReviewsQuery = "SELECT r.*, DATE_FORMAT(postDate, '%b %d, %Y') as post_date, CONCAT(u.firstName, ' ', u.lastName) as userName, u.user_id user_id "
                + "FROM review r, user u "
                + "WHERE r.movie_id = ? "
                + "AND r.user_id = u.user_id "
                + "AND r.user_id " + operator + " ? ";

        List<Review> reviews = null;

        try (Connection conn = dataSource.getConnection(); PreparedStatement pstmtReviews = conn.prepareStatement(movieReviewsQuery)) {
            pstmtReviews.setString(1, movieId);
            pstmtReviews.setString(2, currentUserId);
            try (ResultSet reviewsRs = pstmtReviews.executeQuery()) {
                while (reviewsRs.next()) {
                    if (reviews == null) {
                        reviews = new ArrayList<>();
                    }
                    String userName = reviewsRs.getString("userName");
                    String userId = reviewsRs.getString("user_id");
                    String reviewId = reviewsRs.getString("review_id");
                    String content = reviewsRs.getString("content");
                    String postDate = reviewsRs.getString("post_date");
                    Double rating = reviewsRs.getDouble("rating");

                    Review review = new Review(userName, userId, reviewId, movieId, content, postDate, rating, forUser);
                    reviews.add(review);
                }
            }
        }
        return reviews;
    }

    public boolean isInList(String userId, String movieId) {
        final String sql = "SELECT * "
                + "FROM user_movie_list "
                + "WHERE user_id = ? AND movie_id = ?";

        try (Connection conn = dataSource.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Following line replaces the first place holder with username.
            pstmt.setString(1, userId);
            pstmt.setString(2, movieId);

            try (ResultSet rs = pstmt.executeQuery()) {
                // Traverse the result rows one at a time.
                // Note: This specific while loop will only run at most once 
                // since user_id and movie_id are unique together
                if (rs.next()) {
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String addToList(String userId, String movieId) {
        if (isInList(userId, movieId)) {
            return "already existed";
        }

        final String sql = "INSERT INTO user_movie_list (user_id, movie_id) VALUES (?, ?)";

        try (Connection conn = dataSource.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, userId);
            pstmt.setString(2, movieId);

            if (pstmt.executeUpdate() == 1) {
                return "success";
            } else {
                return "error";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "error";
        }
    }

    public String removeFromList(String userId, String movieId) {
        if (!isInList(userId, movieId)) {
            return "already removed";
        }

        final String sql = "DELETE FROM user_movie_list where user_id = ? and movie_id = ?";

        try (Connection conn = dataSource.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, userId);
            pstmt.setString(2, movieId);

            if (pstmt.executeUpdate() == 1) {
                return "success";
            } else {
                return "error";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "error";
        }
    }

    public boolean postReview(String userId, String movieId, double rating, String content) {
        final String sql = "INSERT INTO Review (user_id, movie_id, content, rating, postDate) VALUES (?, ?, ?, ?, NOW())";

        try (Connection conn = dataSource.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, userId);
            pstmt.setString(2, movieId);
            pstmt.setString(3, content);
            pstmt.setDouble(4, rating);

            return pstmt.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Theoretically, this method isn't needed, but it makes the controller
     * logic for the error messages easier
     */
    public boolean isUserReviewOwner(String userId, String reviewId) {
        final String sql = "SELECT COUNT(*) FROM review WHERE review_id = ? AND user_id = ?";

        try (Connection conn = dataSource.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, userId);
            pstmt.setString(2, reviewId);

            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteReview(String reviewId) {
        final String sql = "DELETE FROM review WHERE review_id = ?";

        try (Connection conn = dataSource.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, reviewId);
            if (pstmt.executeUpdate() == 1) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateReview(String reviewId, String content, double rating) {
        final String sql = "UPDATE review SET content = ?, rating = ? WHERE review_id = ?";

        try (Connection conn = dataSource.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, content);
            pstmt.setDouble(2, rating);
            pstmt.setString(3, reviewId);

            return pstmt.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Review getReview(String movieId, String reviewId) {
        final String sql = "SELECT r.*, CONCAT(firstName, ' ', lastName) as userName "
        + "FROM review r, user u "
        + "WHERE review_id = ?"
        + "AND r.user_id = u.user_id";
        Review review = null;
        try (Connection conn = dataSource.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, reviewId);
            ResultSet reviewsRs = pstmt.executeQuery();
            if (reviewsRs.next()) {
                String userName = reviewsRs.getString("userName");
                String userId = reviewsRs.getString("user_id");
                String content = reviewsRs.getString("content");
                String postDate = reviewsRs.getString("postDate");
                Double rating = reviewsRs.getDouble("rating");

                review = new Review(userName, userId, reviewId, movieId, content, postDate, rating, true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return review;
    }
}
