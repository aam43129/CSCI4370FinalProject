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

@Controller
@RequestMapping("/recommendations")
public class RecommendationController {

    private final RecommendationService recommendationService;
    private final UserService userService;

    @Autowired
    public RecommendationController(RecommendationService recommendationService, UserService userService) {
        this.recommendationService = recommendationService;
        this.userService = userService;
    }

    @GetMapping
    public ModelAndView webpage(@RequestParam(defaultValue = "1") int page,
            @RequestParam(name = "sortBy", required = false) String sortBy,
            @RequestParam(name = "sortByOrder", required = false) String sortByOrder,
            @RequestParam(name = "minRating", required = false) String minRating,
            @RequestParam(name = "genre", required = false) String[] genres,
            @RequestParam(name = "prodCompany", required = false) String prodCompany,
            @RequestParam(name = "error", required = false) String error) {
        ModelAndView mv = new ModelAndView("recommendation_page");

        String currentUserId = userService.getLoggedInUser().getUserId();

        String query = RecommendationService.buildQuery(currentUserId, sortBy, sortByOrder, minRating, genres, prodCompany);
        List<Object> filterParams = RecommendationService.buildParams(minRating, genres, prodCompany);
        RecommendationResponse response = recommendationService.getMovies(currentUserId, page, query, filterParams);

        List<Movie> movies = response.getMovies();
        int totalCount = response.getTotalCount();
        int totalPages = (int) Math.ceil(totalCount / (double) 20);

        List<Page> pagination = UtilityService.getPages(page, totalPages);

        mv.addObject("movies", movies);
        mv.addObject("pagination", pagination);

        String prev_page = (page == 1) ? null : (page - 1) + "";
        String next_page = (page == totalPages) ? null : (page + 1) + "";
        mv.addObject("prev_page", prev_page);
        mv.addObject("next_page", next_page);

        String errorMessage = error;
        mv.addObject("errorMessage", errorMessage);

        if (movies.isEmpty()) {
            mv.addObject("isNoContent", true);
        }

        mv.addObject("isPopularity", "popularity".equals(sortBy));
        mv.addObject("isRating", "rating".equals(sortBy));
        mv.addObject("isRuntime", "runtime".equals(sortBy));
        mv.addObject("isRevenue", "revenue".equals(sortBy));
        mv.addObject("isReleaseDate", "releaseDate".equals(sortBy));
        mv.addObject("isAsc", "asc".equals(sortByOrder));
        mv.addObject("minRating", minRating);
        mv.addObject("prodCompany", prodCompany);

        StringBuilder baseUrl = new StringBuilder("/recommendations?");
        if (sortBy != null && !sortBy.isEmpty()) {
            baseUrl.append("sortBy=").append(sortBy).append("&");
        }
        if (sortByOrder != null && !sortByOrder.isEmpty()) {
            baseUrl.append("sortByOrder=").append(sortByOrder).append("&");
        }
        if (minRating != null && !minRating.isEmpty()) {
            baseUrl.append("minRating=").append(minRating).append("&");
        }
        if (genres != null) {
            for (String genre : genres) {
                baseUrl.append("genre=").append(genre).append("&");
                mv.addObject("is" + genre.substring(0, 1).toUpperCase() + genre.substring(1), true);
            }
        }
        if (prodCompany != null && !prodCompany.isEmpty()) {
            baseUrl.append("prodCompany=").append(prodCompany).append("&");
        }

        mv.addObject("baseUrl", baseUrl.toString());

        return mv;
    }

}
