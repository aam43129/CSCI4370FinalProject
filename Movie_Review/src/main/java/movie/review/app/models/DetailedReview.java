package movie.review.app.models;

/**
 * Represents a movie. This class represents a movie page.
 */
public class DetailedReview extends Review {

    private final Movie movie;

    
    /**
     * Constructs a Comment with specified details, leveraging the BasicPost
     * structure.
     *
     * @param userName the first and last name of the user who wrote the review
     * @param userId  the unique identifier of the user who wrote the review
     * @param reviewId  the unique identifier of the review
     * @param movieId  the unique identifier of the movie that the review is about
     * @param content the content of the review
     * @param postDate the date that the review was posted
     * @param rating the date that the review was posted
     * @param isUserReview is a review made by the user
     */
    public DetailedReview(String userName, String userId, String reviewId, String movieId, String content, String postDate, double rating, Boolean isUserReview, Movie movie) {
        super(userName, userId, reviewId, movieId, content, postDate, rating, isUserReview);
        this.movie = movie;
    }

    public Movie getMovie() {
        return this.movie;
    }
}
