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

    @GetMapping("/{movieId}/{reviewId}/edit")
    public String editReview(@PathVariable("movieId") String movieId,
            @PathVariable String reviewId) {
        String currentUserId = userService.getLoggedInUser().getUserId();

        // boolean isAuthorized = movieService.getIsAuthorized(currentUserId, reviewId)
        boolean isAuthorized = reviewId.equals(movieId); // this is a placeholder for the above

        ModelAndView mv = new ModelAndView();
        if (isAuthorized) {
            // this is my current guess for the implementation
            // get review info in a review object
            // mv.addObject("review", review); 
            // add the rating, so the stars can update
            // String rating = movieService.getRating(review.getRating); // should return rating but rounded down to nearest whole or half (ex: 4.3 -> 4 or 4.7->4.5)
            // mv.addObject("rating" + rating, true);
            return "edit_review_page";
        } else {
            String message = URLEncoder.encode("Unauthorized access",
                    StandardCharsets.UTF_8);
            return "redirect:/movies/" + movieId + "/?error=" + message;
        }
    }

    @PostMapping("/{movieId}/{reviewId}/update")
    public String updateReview(@PathVariable String movieId,
            @PathVariable String reviewId,
            @PathVariable String content,
            @PathVariable double rating) {

        // boolean isAuthorized = movieService.getIsAuthorized(currentUserId, reviewId)
        boolean isAuthorized = reviewId.equals(movieId); // this is a placeholder for the above

        if (isAuthorized) {
            // this is my current guess for the implementation
            // update review info with new content and rating
            return "redirect:/movies/" + movieId;
        } else {
            String message = URLEncoder.encode("Unauthorized access",
                    StandardCharsets.UTF_8);
            return "redirect:/movies/" + movieId + "/" + reviewId + "edit/?error=" + message;
        }
    }

    @PostMapping("/{movieId}/{reviewId}/delete")
    public String deleteReview(@PathVariable String movieId,
            @PathVariable String reviewId) {

        // boolean isAuthorized = movieService.getIsAuthorized(currentUserId, reviewId)
        boolean isAuthorized = reviewId.equals(movieId); // this is a placeholder for the above

        if (isAuthorized) {
            // this is my current guess for the implementation
            // delete review info with new content and rating
            return "redirect:/movies/" + movieId;
        } else {
            String message = URLEncoder.encode("Unauthorized access",
                    StandardCharsets.UTF_8);
            return "redirect:/movies/" + movieId + "/?error=" + message;
        }
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