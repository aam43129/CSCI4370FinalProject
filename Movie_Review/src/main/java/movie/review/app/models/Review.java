package movie.review.app.models;

import movie.review.app.services.UtilityService;

/**
 * Represents a review.
 */
public class Review {

    private final String userName;
    private final String userId;
    private final String reviewId;
    private final String movieId;
    private final String content;
    private final String postDate;
    private final double rating; // 
    private final Boolean isUserReview; // 

    private String[] reviewStars; // for the UI

    /**
     * Constructs a Comment with specified details, leveraging the BasicPost
     * structure.
     *
     * @param userName the first and last name of the user who wrote the review
     * @param userId the unique identifier of the user who wrote the review
     * @param reviewId the unique identifier of the review
     * @param movieId the unique identifier of the movie that the review is
     * about
     * @param content the content of the review
     * @param postDate the date that the review was posted
     * @param rating the date that the review was posted
     * @param isUserReview is a review made by the user
     */
    public Review(String userName, String userId, String reviewId, String movieId, String content, String postDate, double rating, Boolean isUserReview) {
        this.userName = userName;
        this.userId = userId;
        this.reviewId = reviewId;
        this.movieId = movieId;
        this.content = content;
        this.postDate = postDate;
        this.rating = rating;
        this.isUserReview = isUserReview;

        this.reviewStars = UtilityService.getStarClassStrings(5, rating);
    }

    public String getUserName() {
        return this.userName;
    }

    public String getUserId() {
        return this.userId;
    }

    public String getMovieID() {
        return this.movieId;
    }

    public String getContent() {
        return this.content;
    }

    public double getRating() {
        return this.rating;
    }

    public boolean getIsUserReview() {
        return this.isUserReview;
    }
}
