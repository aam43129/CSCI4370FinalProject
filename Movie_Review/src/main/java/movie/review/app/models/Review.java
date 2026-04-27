package movie.review.app.models;

import movie.review.app.services.UtilityService;

/**
 * Represents a review.
 */
public class Review {

    // In our db, a movie should also have review_id and movie_id, but
    // because those aren't used in our mustache files, I won't add them.
    private final String userName;
    private final String content;
    private final String postDate;
    private final double rating; // 

    private String[] reviewStars; // for the UI

    /**
     * Constructs a Comment with specified details, leveraging the BasicPost
     * structure.
     *
     * @param userName the first and last name of the user who wrote the review
     * @param content the content of the review
     * @param postDate the date that the review was posted
     * @param rating the date that the review was posted
     */
    public Review(String userName, String content, String postDate, double rating) {
        this.userName = userName;
        this.content = content;
        this.postDate = postDate;
        this.rating = rating;

        this.reviewStars = UtilityService.getStarClassStrings(5, rating);
    }
}
