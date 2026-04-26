package movie.review.app.models;

/**
 * Represents a movie.
 * This class represents a movie page.
 */
public class DetailedMovie extends Movie {

    private final String release_date;
    private final String homepage;
    private final String original_language;
    private final String original_title;
    private final String overview;



    /**
     * Constructs a Comment with specified details, leveraging the BasicPost structure.
     *
     * @param movieId    the unique identifier of the movie
     * @param title      the title of the movie
     * @param poster     the link for the movie's poster
     * @param tagline    the tagline of the movie
     * @param isListed   is the movie in the user's list
     * 
     * @param release_date   
     * @param homepage   
     * @param original_language   
     * @param original_title   
     * @param overview   
     */
    public DetailedMovie(String movieId, String title, String poster, String tagline, Boolean isListed, String release_date, String homepage, String original_language, String original_title, String overview) {
        super(movieId, title, poster, tagline, isListed);
        this.release_date = release_date;
        this.homepage = homepage;
        this.original_language = original_language;
        this.original_title = original_title;
        this.overview = overview;
    }
}
