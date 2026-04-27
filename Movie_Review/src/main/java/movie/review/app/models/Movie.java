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

    /* The tagline for the movie */
    private String tagline;

    /* Is the movie in the user's list */
    private Boolean isListed;


    /**
     * Constructs a Comment with specified details, leveraging the BasicPost structure.
     *
     * @param movieId    the unique identifier of the movie
     * @param title      the title of the movie
     * @param poster     the link for the movie's poster
     * @param tagline    the tagline of the movie
     * @param isListed   is the movie in the user's list
     */
    public Movie(String movieId, String title, String poster, String tagline, Boolean isListed) {
        this.movieId = movieId;
        this.title = title;
        this.poster = poster;
        this.tagline = tagline;
        this.isListed = isListed;
    }

    public String getMovieId() {
        return movieId;
    }

    public String getTitle() {
        return title;
    }

    public String getPoster() {
        return poster;
    }

    public String getTagline() {
        return tagline;
    }

    public boolean getIsListed() {
        return isListed;
    }
}
