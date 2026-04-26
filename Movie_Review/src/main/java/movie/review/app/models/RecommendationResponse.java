package movie.review.app.models;

import java.util.List;

public class RecommendationResponse {

    private final List<Movie> movies;
    private final int totalCount;

    public RecommendationResponse(List<Movie> movies, int totalCount) {
        this.movies = movies;
        this.totalCount = totalCount;
    }

    public List<Movie> getMovies() {
        return movies;
    }

    public int getTotalCount() {
        return totalCount;
    }
}
