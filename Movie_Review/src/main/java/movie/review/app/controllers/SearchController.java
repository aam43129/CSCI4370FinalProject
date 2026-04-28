package movie.review.app.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import movie.review.app.models.Movie;
import movie.review.app.services.SearchService;
import movie.review.app.services.UserService;

@Controller
@RequestMapping("/search")
public class SearchController {

    private final SearchService searchService;
    private final UserService userService;

    @Autowired
    public SearchController(SearchService searchService, UserService userService) {
        this.searchService = searchService;
        this.userService = userService;
    }

    @GetMapping
    public ModelAndView webpage(@RequestParam(name = "query") String query) {
        ModelAndView mv = new ModelAndView("search_page");

        List<Movie> movies = searchService.getMovies(query);

        mv.addObject("movies", movies);
        
        return mv;
    }
}
