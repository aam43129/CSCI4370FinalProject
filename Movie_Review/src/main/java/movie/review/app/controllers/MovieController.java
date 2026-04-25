package movie.review.app.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/movie")
public class MovieController {

    @GetMapping("/{movieId}")
    public ModelAndView webpage(@PathVariable("movieId") String movieId,
            @RequestParam(name = "error", required = false) String error) {
        return new ModelAndView("movie_page");
    }
}
