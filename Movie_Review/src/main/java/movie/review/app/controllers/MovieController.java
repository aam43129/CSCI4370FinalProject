package movie.review.app.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import movie.review.app.services.MovieService;
import movie.review.app.services.UserService;

import movie.review.app.models.DetailedMovie;

@Controller
@RequestMapping("/movies")
public class MovieController {

    private final MovieService movieService;
    private final UserService userService;

    @Autowired
    public MovieController(MovieService movieService, UserService userService) {
        this.movieService = movieService;
        this.userService = userService;
    }

    @GetMapping("/{movieId}")
    public ModelAndView webpage(@PathVariable("movieId") String movieId,
            @RequestParam(name = "error", required = false) String error) {
        ModelAndView mv = new ModelAndView("movie_page");

        String currentUserId = userService.getLoggedInUser().getUserId();

        DetailedMovie movie = movieService.getMovies(currentUserId, movieId);

        mv.addObject("movie", movie);

        String errorMessage = error;
        mv.addObject("errorMessage", errorMessage);
        
        return mv;
    }
}
