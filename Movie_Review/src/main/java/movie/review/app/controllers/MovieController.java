package movie.review.app.controllers;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import movie.review.app.models.DetailedMovie;
import movie.review.app.services.MovieService;
import movie.review.app.services.UserService;

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

    @GetMapping("/{movieId}/list/{isAdd}")
    public String addOrRemoveList(@PathVariable("movieId") String movieId,
            @PathVariable("isAdd") Boolean isAdd) {
        String currentUserId = userService.getLoggedInUser().getUserId();

        String status = isAdd
                ? movieService.addToList(currentUserId, movieId)
                : movieService.removeFromList(currentUserId, movieId);

        if (status.equals("success")) {
            return "redirect:/movies/" + movieId;
        } else if (status.equals("already existed") || status.equals("already removed")) {
            String error = isAdd ? "That movie already existed your list." : "That movie w your list.";
            String message = URLEncoder.encode(error, StandardCharsets.UTF_8);
            return "redirect:/movies/" + movieId + "?error=" + message;
        } else {
            String error = isAdd ? "Failed to add movie to your list." : "Failed to remove movie from your list.";
            String message = URLEncoder.encode(error + " Please try again.",
                    StandardCharsets.UTF_8);
            return "redirect:/movies/" + movieId + "?error=" + message;
        }
    }
  
