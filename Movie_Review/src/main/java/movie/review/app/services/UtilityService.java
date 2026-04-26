package movie.review.app.services;

import java.util.ArrayList;
import java.util.List;
import java.sql.PreparedStatement;

import movie.review.app.models.Page;

public class UtilityService {

    public static String getListedQuery() {
        String isListedQuery
                = ", ("
                + "SELECT COUNT(*) = 1 "
                + "FROM user_movie_list uml "
                + "WHERE uml.user_id = ? "
                + "AND uml.movie_id = m.movie_id"
                + ") as isListed ";
        return isListedQuery;
    }

    /*
    * This method is for returning the pagination.
    * eg: for 8 pages: 1, 2, 3, 4, ... 7, 8
     */
    public static List<Page> getPages(int pageNum, int numResults) {

        int totalPages = (int) Math.ceil(numResults / (double) 20);

        List<Page> pagination = new ArrayList<>();

        for (int i = 1; i <= totalPages; i++) {
            // Only add first 4 pages and the last 2 pages
            if (i <= 4 || i > totalPages - 2) {
                pagination.add(new Page(i, i == pageNum));
            } // Add the "..." separator one time after page 4
            else if (i == 5) {
                pagination.add(Page.setSeparator());
            }
        }

        return pagination;
    }

    public static String[] getStarClassStrings(int maxRating, double actualRating) {
        String[] starClasses = new String[maxRating];

        for (int i = 1; i <= maxRating; i++) {
            if (actualRating >= i) {
                // Full Star
                starClasses[i - 1] = "fa-star";
            } else if (actualRating >= i - 0.5) {
                // Half Star
                starClasses[i - 1] = "fa-star-half-o";
            } else {
                // Empty Star
                starClasses[i - 1] = "fa-star-o";
            }
        }
        return starClasses;
    }
}
