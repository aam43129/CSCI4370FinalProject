package movie.review.app.models;

import java.util.List;

import movie.review.app.services.UtilityService;

/**
 * Represents a movie. This class represents a movie page.
 */
public class DetailedMovie extends Movie {

    private final double vote_average;
    private final int vote_count;
    private final String release_date;
    private final long revenue;
    private final long budget;
    private final String homepage;
    private final int runtime;
    private final String original_language;
    private final String original_title;
    private final String overview;
    private final double popularity;
    private final double avg_rating;
    public final List<String> genres;
    public final List<String> productionCompanies;
    public final List<Review> reviewMadeByUser; // List<Review> for compatability with a method in MovieService
    public final List<Review> reviewsMadeByOthers;

    private final Boolean showOgTitle;
    private final String[] imdb_star_classes;
    private final String[] avg_rating_star_classes;
    private final int hours;
    private final int minutes;
    public final String formatted_budget;
    public final String formatted_revenue;

    /**
     * Constructs a Comment with specified details, leveraging the BasicPost
     * structure.
     *
     * @param movieId the unique identifier of the movie
     * @param title the title of the movie
     * @param poster the link for the movie's poster
     * @param tagline the tagline of the movie
     * @param isListed is the movie in the user's list
     *
     * @param vote_average
     * @param vote_count
     * @param release_date
     * @param revenue
     * @param budget
     * @param homepage
     * @param runtime
     * @param original_language
     * @param original_title
     * @param overview
     * @param popularity
     */
    public DetailedMovie(String movieId, String title, String poster, String tagline, Boolean isListed,
            double vote_average, int vote_count, String release_date, long revenue, long budget, String homepage,
            int runtime, String original_language, String original_title, String overview, double popularity,
            double avg_rating, List<String> genres, List<String> productionCompanies, List<Review> reviewMadeByUser, List<Review> reviewsMadeByOthers) {
        super(movieId, title, poster, tagline, isListed);
        this.vote_average = vote_average;
        this.vote_count = vote_count;
        this.release_date = release_date;
        this.revenue = revenue;
        this.budget = budget;
        this.homepage = homepage;
        this.runtime = runtime;
        this.original_language = original_language;
        this.original_title = original_title;
        this.overview = overview;
        this.popularity = popularity;
        this.avg_rating = avg_rating;
        this.genres = genres;
        this.productionCompanies = productionCompanies;
        this.reviewMadeByUser = reviewMadeByUser;
        this.reviewsMadeByOthers = reviewsMadeByOthers;

        this.showOgTitle = !(title.equals(original_title));
        this.imdb_star_classes = UtilityService.getStarClassStrings(10, vote_average);
        this.avg_rating_star_classes = UtilityService.getStarClassStrings(5, avg_rating);
        this.hours = runtime / 60;
        this.minutes = runtime % 60;
        this.formatted_budget = formatLargeNum(budget);
        this.formatted_revenue = formatLargeNum(revenue);
    }

    public String formatLargeNum(double num) {
        String ret = "";
        if (num / 1_000_000_000 >= 1) {
            ret += String.format("%.2f", num / 1_000_000_000) + "B";
        } else if (num / 1_000_000 >= 1) {
            ret += String.format("%.1f", num / 1_000_000) + "M";
        } else if (num / 1_000 >= 1) {
            ret += String.format("%.1f", num / 1_000) + "K";
        } else {
            ret += num;
        }
        return ret;
    }
}
