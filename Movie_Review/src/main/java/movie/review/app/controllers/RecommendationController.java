/**
 * Copyright (c) 2024 Sami Menik, PhD. All rights reserved.
 *
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
import movie.review.app.models.Page;
import movie.review.app.models.RecommendationResponse;

import movie.review.app.services.RecommendationService;
import movie.review.app.services.UserService;
import movie.review.app.services.UtilityService;

/**
 * This controller handles the home page and some of it's sub URLs.
 */
@Controller
@RequestMapping("/recommendations")
public class RecommendationController {

    private final RecommendationService recommendationService;
    private final UserService userService;

    /**
     * This is the specific function that handles the root URL itself.
     *
     * Note that this accepts a URL parameter called error. The value to this
     * parameter can be shown to the user as an error message. See notes in
     * HashtagSearchController.java regarding URL parameters.
     */
    // private final UserService userService;
    @Autowired
    public RecommendationController(RecommendationService recommendationService, UserService userService) {
        this.recommendationService = recommendationService;
        this.userService = userService;
    }

    @GetMapping
    public ModelAndView webpage(@RequestParam(defaultValue = "1") int page, // for the pagination
            @RequestParam(name = "sortBy", required = false) String sortBy,
            @RequestParam(name = "sortByOrder", required = false) String sortByOrder,
            @RequestParam(name = "minRating", required = false) String minRating,
            @RequestParam(name = "genre", required = false) String[] genres,
            @RequestParam(name = "prodCompany", required = false) String prodCompany,
            @RequestParam(name = "error", required = false) String error) {
        ModelAndView mv = new ModelAndView("recommendation_page");

        String currentUserId = userService.getLoggedInUser().getUserId();

        String query = RecommendationService.buildQuery(currentUserId, sortBy, sortByOrder, minRating, genres, prodCompany);
        RecommendationResponse response = recommendationService.getMovies(currentUserId, page, query);

        List<Movie> movies = response.getMovies();
        int totalCount = response.getTotalCount();

        List<Page> pagination = UtilityService.getPages(page, totalCount);

        mv.addObject("movies", movies);
        mv.addObject("pagination", pagination);

        // If an error occured, you can set the following property with the
        // error message to show the error message to the user.
        // An error message can be optionally specified with a url query parameter too.
        String errorMessage = error;
        mv.addObject("errorMessage", errorMessage);

        // if there is no post to display, then give a no post comment
        if (movies.isEmpty()) {
            mv.addObject("isNoContent", true);
        } //if 

        mv.addObject("isPopularity", "popularity".equals(sortBy));
        mv.addObject("isRating", "rating".equals(sortBy));
        mv.addObject("isRuntime", "runtime".equals(sortBy));
        mv.addObject("isRevenue", "revenue".equals(sortBy));
        mv.addObject("isReleaseDate", "releaseDate".equals(sortBy));
        mv.addObject("isAsc", "asc".equals(sortByOrder));

        // Build base URL for pagination to preserve filters
        StringBuilder baseUrl = new StringBuilder("/recommendations?");
        if (sortBy != null && !sortBy.isEmpty())
            baseUrl.append("sortBy=").append(sortBy).append("&");
        if (sortByOrder != null && !sortByOrder.isEmpty())
            baseUrl.append("sortByOrder=").append(sortByOrder).append("&");
        if (minRating != null && !minRating.isEmpty())
            baseUrl.append("minRating=").append(minRating).append("&");
        if (genres != null) {
            for (String genre : genres) {
                baseUrl.append("genre=").append(genre).append("&");
            }
        }
        if (prodCompany != null && !prodCompany.isEmpty())
            baseUrl.append("prodCompany=").append(prodCompany).append("&");

        mv.addObject("baseUrl", baseUrl.toString());

        return mv;
    }

}
