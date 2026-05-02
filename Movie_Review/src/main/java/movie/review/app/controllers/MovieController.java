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
import movie.review.app.models.Review;
import movie.review.app.services.MovieService;
import movie.review.app.services.UserService;
import movie.review.app.services.UtilityService;

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

    @GetMapping("/{movieId}/reviews/{reviewId}/edit")
    public ModelAndView editReview(@PathVariable("movieId") String movieId,
            @PathVariable String reviewId) {
        String currentUserId = userService.getLoggedInUser().getUserId();

        boolean isAuthorized = movieService.isUserReviewOwner(currentUserId, reviewId);

        if (isAuthorized) {
            // get review info in a review object

            Review review = movieService.getReview(movieId, reviewId);

            if (review != null) {
                ModelAndView mv = new ModelAndView("edit_review_page");
                mv.addObject("review", review);

                double rating = UtilityService.getFormattedRating(review.getRating());
                mv.addObject("rating" + rating, true);
                return mv;
            } else {
                // theoretically, this should not happen as isAuthorized made sure
                // there was a review by this user with that reviewId
                String message = URLEncoder.encode("Review not found",
                        StandardCharsets.UTF_8);
                return new ModelAndView("redirect:/movies/" + movieId + "/?error=" + message);
            }
        } else {
            String message = URLEncoder.encode("Unauthorized access",
                    StandardCharsets.UTF_8);
            return new ModelAndView("redirect:/movies/" + movieId + "/?error=" + message);
        }
    }

    @PostMapping("/{movieId}/reviews/{reviewId}/update")
    public String updateReview(@PathVariable String movieId,
            @PathVariable String reviewId,
            @RequestParam String content,
            @RequestParam double rating) {

        String currentUserId = userService.getLoggedInUser().getUserId();

        boolean isAuthorized = movieService.isUserReviewOwner(currentUserId, reviewId);

        if (isAuthorized) {
            // delete review info
            boolean isSuccessful = movieService.updateReview(reviewId, content, rating);
            if (isSuccessful) {
                return "redirect:/movies/" + movieId;
            } else {
                // Redirect the user with an error message if there was an error.
                String message = URLEncoder.encode("Failed to update review. Please try again.",
                        StandardCharsets.UTF_8);
                return "redirect:/movies/" + movieId + "?error=" + message;
            }
        } else {
            String message = URLEncoder.encode("Unauthorized access",
                    StandardCharsets.UTF_8);
            return "redirect:/movies/" + movieId + "/reviews/" + reviewId + "/?error=" + message;
        }
    }

    @PostMapping("/{movieId}/reviews/{reviewId}/delete")
    public String deleteReview(@PathVariable String movieId,
            @PathVariable String reviewId) {

        String currentUserId = userService.getLoggedInUser().getUserId();

        boolean isAuthorized = movieService.isUserReviewOwner(currentUserId, reviewId);

        if (isAuthorized) {
            // delete review info
            boolean isSuccessful = movieService.deleteReview(reviewId);
            if (isSuccessful) {
                return "redirect:/movies/" + movieId;
            } else {
                // Redirect the user with an error message if there was an error.
                String message = URLEncoder.encode("Failed to delete review. Please try again.",
                        StandardCharsets.UTF_8);
                return "redirect:/movies/" + movieId + "?error=" + message;
            }
        } else {
            String message = URLEncoder.encode("Unauthorized access",
                    StandardCharsets.UTF_8);
            return "redirect:/movies/" + movieId + "/?error=" + message;
        }
    }

    @PostMapping("/{movieId}/reviews/create-review")
    public String postReview(@PathVariable("movieId") String movieId,
            @RequestParam(name = "rating") double rating,
            @RequestParam(name = "content") String content) {
        String userId = userService.getLoggedInUser().getUserId();

        boolean isSucessful = movieService.postReview(userId, movieId, rating, content);
        // Redirect the user if the comment adding is a success.
        if (isSucessful) {
            return "redirect:/movies/" + movieId;
        }

        // Redirect the user with an error message if there was an error.
        String message = URLEncoder.encode("Failed to post the comment. Please try again.",
                StandardCharsets.UTF_8);
        return "redirect:/movies/" + movieId + "?error=" + message;
    }

}
