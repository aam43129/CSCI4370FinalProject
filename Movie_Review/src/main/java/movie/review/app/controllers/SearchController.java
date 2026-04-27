package movie.review.app.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/search")
public class SearchController {

    @GetMapping
    public ModelAndView webpage(@RequestParam(name = "hashtags") String hashtags) {
        return new ModelAndView("search_page");
    }
}
