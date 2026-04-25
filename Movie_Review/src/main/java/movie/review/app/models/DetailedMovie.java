package movie.review.app.models;

/**
 * Represents a movie.
 * This class represents a movie page.
 */
public class DetailedMovie extends Movie {




    /**
     * Constructs a Comment with specified details, leveraging the BasicPost structure.
     *
     * @param movieId    the unique identifier of the movie
     * @param title      the title of the movie
     * @param poster     the link for the movie's poster
     */
    public DetailedMovie(String movieId, String title, String poster) {
        super(movieId, title, poster);
    }
}
