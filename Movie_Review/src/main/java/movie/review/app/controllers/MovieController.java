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

    @GetMapping("/{movieId}/list/{isAdd}")
    public String addOrRemoveList(@PathVariable("movieId") String movieId,
            @PathVariable("isAdd") Boolean isAdd) {
        String currentUserId = userService.getLoggedInUser().getUserId();

        // boolean isSuccess = isAdd
        //         ? movieService.addToList(currentUserId, movieId)
        //         : movieService.removeFromList(currentUserId, movieId);
        boolean isSuccess = isAdd;

        if (isSuccess) {
            return "redirect:/movies/" + movieId;
        }
        String message = URLEncoder.encode("Failed to (un)like the post. Please try again.",
                StandardCharsets.UTF_8);
        return "redirect:/post/" + movieId + "?error=" + message;
    }

    @PostMapping("/{movieId}/create-review")
    public String postReview(@PathVariable("movieId") String movieId,
            @RequestParam(name = "rating") String rating,
            @RequestParam(name = "content") String content) {
        System.out.println("The user is attempting add a comment:");
        System.out.println("\tmovieId: " + movieId);
        System.out.println("\trating: " + rating);
        System.out.println("\tcontent: " + content);
        String userId = userService.getLoggedInUser().getUserId();

        // boolean postSuccessful = movieService.postReview(movieId, userId, rating, content);
        boolean postSuccessful = true;
        // Redirect the user if the comment adding is a success.
        if (postSuccessful) {
            return "redirect:/movies/" + movieId;
        }

        // Redirect the user with an error message if there was an error.
        String message = URLEncoder.encode("Failed to post the comment. Please try again.",
                StandardCharsets.UTF_8);
        return "redirect:/movies/" + movieId + "?error=" + message;
    }

}

