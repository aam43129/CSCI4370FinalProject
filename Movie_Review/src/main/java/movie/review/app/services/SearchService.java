package movie.review.app.services;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import movie.review.app.models.Movie;

@Service
public class SearchService {

    private final DataSource dataSource;

    @Autowired
    public SearchService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<Movie> getMovies(String query) {
        return new ArrayList<>();
    }

}
