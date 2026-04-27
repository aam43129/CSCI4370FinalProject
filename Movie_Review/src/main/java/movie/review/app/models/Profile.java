package movie.review.app.models;

import java.util.List;

/**
 * Represents a movie.
 * This class is very basic because it represents a movie card.
 */
public class Profile {

    private final String userName;
    private final List<Movie> movies;
    private final List<DetailedReview> reviews;
    private final Boolean isCurrentUserProfile;

    /**
     * Constructs a Comment with specified details, leveraging the BasicPost structure.
     *
     * @param userName              the firstName + lastName of the user
     * @param movies               the movies the user has listed
     * @param detailedReviews      the detailed reviews
     * @param isCurrentUserProfile   if it's the profile of the currently logged in user
     */
    public Profile(String userName, List<Movie> movies, List<DetailedReview> reviews, Boolean isCurrentUserProfile) {
        this.userName = userName;
        this.movies = movies;
        this.reviews = reviews;
        this.isCurrentUserProfile = isCurrentUserProfile;
    }
}
