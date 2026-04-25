package movie.review.app.models;

/**
 * Represents a movie.
 * This class is very basic because it represents a movie card.
 */
public class Movie {

    /* The unique identifier for a movie */
    private String movieId;

    /* The title of the movie */
    private String title;

    /* The link for the movie's poster */
    private String poster;

    /**
     * Constructs a Comment with specified details, leveraging the BasicPost structure.
     *
     * @param movieId    the unique identifier of the movie
     * @param title      the title of the movie
     * @param poster     the link for the movie's poster
     */
    public Movie(String movieId, String title, String poster) {
        this.movieId = movieId;
        this.title = title;
        this.poster = poster;
    }
}
