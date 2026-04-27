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
        // For all movie_info - production companies and genres
        final String movieInfoQuery
                = "SELECT m.*, AVG(r.rating) avg_rating "
                + UtilityService.getListedQuery()
                + "FROM movie m "
                + "LEFT JOIN review r ON m.movie_id = r.movie_id " // have to use a left join in case the movie doesn't have any reviews left
                + "WHERE m.movie_id = ? "
                + "GROUP BY m.movie_id";

        final String movieGenresQuery
                = "SELECT g.name name "
                + "FROM genre g, movie_genre mg "
                + "WHERE mg.movie_id = ? "
                + "AND g.genre_id = mg.genre_id";

         final String movieReviewsQuery
                = "SELECT r.*, CONCAT(u.firstName, ' ', u.lastName) as userName "
                + "FROM review r, user u "
                + "WHERE r.movie_id = ? "
                + "AND r.user_id = u.user_id";

        final String movieCompaniesQuery
                = "SELECT pc.name name "
                + "FROM production_company pc, movie_company mc "
                + "WHERE mc.movie_id = ? "
                + "AND mc.company_id = pc.company_id";

        DetailedMovie movie = null;

        try (Connection conn = dataSource.getConnection()) {
            PreparedStatement pstmtGenres = conn.prepareStatement(movieGenresQuery);
            pstmtGenres.setString(1, movieId);
            ResultSet genresRs = pstmtGenres.executeQuery();
            List<String> genres = new ArrayList<>();
            while (genresRs.next()) {
                genres.add(genresRs.getString("name"));
            }

            PreparedStatement pstmtCompanies = conn.prepareStatement(movieCompaniesQuery);
            pstmtCompanies.setString(1, movieId);
            ResultSet companyRs = pstmtCompanies.executeQuery();
            List<String> companies = new ArrayList<>();
            while (companyRs.next()) {
                companies.add(companyRs.getString("name"));
            }

            PreparedStatement pstmtReviews = conn.prepareStatement(movieReviewsQuery);
            pstmtReviews.setString(1, movieId);
            ResultSet reviewsRs = pstmtReviews.executeQuery();
            List<Review> reviews = new ArrayList<>();
            while (reviewsRs.next()) {
                String userName = reviewsRs.getString("userName");
                String content = reviewsRs.getString("content");
                String postDate = reviewsRs.getString("postDate");
                Double rating = reviewsRs.getDouble("rating");

                Review review = new Review(userName, content, postDate, rating);
                reviews.add(review);
            }

            PreparedStatement pstmtMovieInfo = conn.prepareStatement(movieInfoQuery);
            pstmtMovieInfo.setString(1, currentUserId); // for the isListed query
            pstmtMovieInfo.setString(2, movieId); // for the isListed query

            ResultSet movieRs = pstmtMovieInfo.executeQuery();
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

                movie = new DetailedMovie(movieId, title, poster, tagline, isListed, vote_average, vote_count, release_date, revenue, budget, homepage, runtime, original_language, original_title, overview, popularity, avg_rating, genres, companies, reviews);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return movie;
    }
}
