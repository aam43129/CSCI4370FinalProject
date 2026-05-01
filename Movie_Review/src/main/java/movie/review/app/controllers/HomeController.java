/**
 * Copyright (c) 2024 Sami Menik, PhD. All rights reserved.
 *
 *  *This is a project developed by Dr. Menik to give the students an opportunity to apply database concepts learned in the class in a real world project. Permission is granted to host a running version of this software and to use images or videos of this work solely for the purpose of demonstrating the work to potential employers. Any form of reproduction, distribution, or transmission of the software's source code, in part or whole, without the prior written consent of the copyright owner, is strictly prohibited.
 */
package movie.review.app.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import movie.review.app.models.Movie;
import movie.review.app.services.HomeService;
import movie.review.app.services.UserService;

/**
 * This controller handles the home page and some of it's sub URLs.
 */
@Controller
@RequestMapping
public class HomeController {

    private final HomeService homeService;
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
    public HomeController(HomeService homeService, UserService userService) {
        this.homeService = homeService;
        this.userService = userService;
    }

    @GetMapping
    public ModelAndView webpage(@RequestParam(name = "error", required = false) String error) {
        // See notes on ModelAndView in BookmarksController.java.
        ModelAndView mv = new ModelAndView("home_page");

        String currentUserId = userService.getLoggedInUser().getUserId();

        //List<Movie> movies = homeService.getMovies(currentUserId);

        // mv.addObject("movies", movies);
       //mv.addObject("movies", movies);

        mv.addObject("trendingMovies", homeService.getMovies(currentUserId));
        List<String> topGenres = homeService.getTopGenresForUser(currentUserId);
        List<GenreSection> genreSections = new ArrayList<>();
        for (String genre : topGenres) {
            List<Movie> genreMovies = homeService.getMovies(currentUserId, genre);
            if (!genreMovies.isEmpty()) {
                genreSections.add(new GenreSection(genre, genreMovies));
            }
        }
        mv.addObject("genreSections", genreSections);

        // If an error occured, you can set the following property with the
        // error message to show the error message to the user.
        // An error message can be optionally specified with a url query parameter too.
        String errorMessage = error;
        mv.addObject("errorMessage", errorMessage);

        // There is no "no-content" object because it is handled in the html
        return mv;
    }

    public static class GenreSection {
        private String genreName;
        private List<Movie> movies;

        public GenreSection(String genreName, List<Movie> movies) {
            this.genreName = genreName;
            this.movies = movies;
        }

        // Getters so Mustache can find the keys {{genreName}} and {{movies}}
        public String getGenreName() { return genreName; }
        public List<Movie> getMovies() { return movies; }
    }

}
