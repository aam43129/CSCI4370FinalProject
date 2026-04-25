/**
 * Copyright (c) 2024 Sami Menik, PhD. All rights reserved.

 *  *This is a project developed by Dr. Menik to give the students an opportunity to apply database concepts learned in the class in a real world project. Permission is granted to host a running version of this software and to use images or videos of this work solely for the purpose of demonstrating the work to potential employers. Any form of reproduction, distribution, or transmission of the software's source code, in part or whole, without the prior written consent of the copyright owner, is strictly prohibited.
 */
package movie.review.app.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import movie.review.app.models.Movie;
import movie.review.app.services.RecommendationService;


/**
 * This controller handles the home page and some of it's sub URLs.
 */
@Controller
@RequestMapping("/recommendations")
public class RecommendationController {

    private final RecommendationService recommendationService;

    /**
     * This is the specific function that handles the root URL itself.
     *
     * Note that this accepts a URL parameter called error. The value to this
     * parameter can be shown to the user as an error message. See notes in
     * HashtagSearchController.java regarding URL parameters.
     */
    // private final UserService userService;
    @Autowired
    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @GetMapping
    public ModelAndView webpage(@RequestParam(defaultValue = "1") int page, // for the pagination
            @RequestParam(name = "sortBy", required = false) String sortBy,
            @RequestParam(name = "sortByOrder", required = false) String sortByOrder,
            @RequestParam(name = "minRating", required = false) String minRating,
            @RequestParam(name = "genre[]", required = false) String[] genres,
            @RequestParam(name = "prodCompany", required = false) String prodCompany,     
            @RequestParam(name = "error", required = false) String error) {     
        ModelAndView mv = new ModelAndView("recommendation_page");

        int offset = (page - 1) * 20; // 20 is the number of movies per page
        List<Movie> movies = recommendationService.getSortedAndFilteredMovies(sortBy, sortByOrder, minRating, genres, prodCompany, offset);
        
        mv.addObject("movies", movies);
        
        // If an error occured, you can set the following property with the
        // error message to show the error message to the user.
        // An error message can be optionally specified with a url query parameter too.
        String errorMessage = error;
        mv.addObject("errorMessage", errorMessage);

        // if there is no post to display, then give a no post comment
        if (movies.isEmpty()) {
            mv.addObject("isNoContent", true);
        } //if 

        return mv;
    }

}
