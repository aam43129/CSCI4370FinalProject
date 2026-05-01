-- 1. USER AUTHENTICATION & REGISTRATION

-- Purpose: Checks if a username exists during Login or Registration.
-- URL Path: Movie_Review/src/main/java/movie/review/app/services/UserService.java
-- URL Path: http://localhost:8080/login
SELECT * FROM user WHERE username = ?;

-- Purpose: Registers a new user account.
-- URL Path: Movie_Review/src/main/java/movie/review/app/services/UserService.java
-- URL Path: http://localhost:8080/register
INSERT INTO user (username, password, firstName, lastName) VALUES (?, ?, ?, ?);

-- 2. HOME PAGE SECTIONS

-- Purpose: Retrieves trending movies based on popularity.
-- URL Path: Movie_Review/src/main/java/movie/review/app/services/HomeService.java
-- URL Path: http://localhost:8080/
SELECT m.movie_id, m.title,
       IFNULL(m.poster, 'https://via.placeholder.com/500x750?text=No+Poster') AS poster,
       IFNULL(m.tagline, '') AS tagline,
       IF(uml.user_id IS NULL, 0, 1) AS isListed
FROM Movie m
         LEFT JOIN user_movie_list uml ON m.movie_id = uml.movie_id AND uml.user_id = ?
WHERE m.vote_count > 100
ORDER BY m.popularity DESC
    LIMIT 14;

-- Purpose: Identifies the top 3 genres for a user based on their high ratings.
-- URL Path: Movie_Review/src/main/java/movie/review/app/services/HomeService.java
-- URL Path: http://localhost:8080/
SELECT g.name FROM Genre g
                       JOIN Movie_Genre mg ON g.genre_id = mg.genre_id
                       LEFT JOIN Review r ON mg.movie_id = r.movie_id AND r.user_id = ?
GROUP BY g.genre_id
ORDER BY IFNULL(AVG(r.rating), 0) DESC, COUNT(mg.movie_id) DESC
    LIMIT 3;

-- Purpose: Fetches popular movies within a specific genre for dynamic home rows.
-- URL Path: Movie_Review/src/main/java/movie/review/app/services/HomeService.java
-- URL Path: http://localhost:8080/
SELECT m.movie_id, m.title,
       IFNULL(m.poster, 'https://via.placeholder.com/500x750?text=No+Poster') AS poster,
       IFNULL(m.tagline, '') AS tagline,
       IF(uml.user_id IS NULL, 0, 1) AS isListed
FROM Movie m
         JOIN Movie_Genre mg ON m.movie_id = mg.movie_id
         JOIN Genre g ON mg.genre_id = g.genre_id
         LEFT JOIN user_movie_list uml ON m.movie_id = uml.movie_id AND uml.user_id = ?
WHERE g.name = ?
ORDER BY m.popularity DESC
    LIMIT 6;


-- 3. MOVIE DETAILS PAGE

-- Purpose: Retrieves metadata and average rating for a specific movie.
-- URL Path: Movie_Review/src/main/java/movie/review/app/services/MovieService.java
-- URL Path: http://localhost:8080/movies/{movieId}
SELECT m.*, AVG(r.rating) AS avg_rating
FROM movie m
         LEFT JOIN review r ON m.movie_id = r.movie_id
WHERE m.movie_id = ?
GROUP BY m.movie_id;

-- Purpose: Fetches the genre list for a specific movie.
-- URL Path: Movie_Review/src/main/java/movie/review/app/services/MovieService.java
-- URL Path: http://localhost:8080/movies/{movieId}
SELECT g.name FROM genre g, movie_genre mg
WHERE mg.movie_id = ? AND g.genre_id = mg.genre_id;

-- Purpose: Fetches production companies for a specific movie.
-- URL Path: Movie_Review/src/main/java/movie/review/app/services/MovieService.java
-- URL Path: http://localhost:8080/movies/{movieId}
SELECT pc.name FROM production_company pc, movie_company mc
WHERE mc.movie_id = ? AND mc.company_id = pc.company_id;

-- Purpose: Retrieves all reviews and user info for a specific movie.
-- URL Path: Movie_Review/src/main/java/movie/review/app/services/MovieService.java
-- URL Path: http://localhost:8080/movies/{movieId}
SELECT r.*, DATE_FORMAT(postDate, '%b %d, %Y') as post_date,
       CONCAT(u.firstName, ' ', u.lastName) as userName, u.user_id user_id
FROM review r, user u
WHERE r.movie_id = ? AND r.user_id = u.user_id;


-- 4. REVIEWS & BOOKMARKS (MODIFICATIONS)

-- Purpose: Submits a new user review.
-- URL Path: Movie_Review/src/main/java/movie/review/app/services/MovieService.java
-- URL Path: http://localhost:8080/movies/{movieId}/reviews/create-review
INSERT INTO Review (user_id, movie_id, content, rating, postDate) VALUES (?, ?, ?, ?, NOW());

-- Purpose: Updates a user's own review.
-- URL Path: Movie_Review/src/main/java/movie/review/app/services/MovieService.java
-- URL Path: http://localhost:8080/movies/{movieId}/reviews/update
UPDATE Review SET content = ?, rating = ? WHERE review_id = ?;

-- Purpose: Deletes a user's own review.
-- URL Path: Movie_Review/src/main/java/movie/review/app/services/MovieService.java
-- URL Path: http://localhost:8080/movies/{movieId}/reviews/{reviewId}/delete
DELETE FROM review WHERE review_id = ?;

-- Purpose: Adds a movie to the user's movie list.
-- URL Path: Movie_Review/src/main/java/movie/review/app/services/MovieService.java
-- URL Path: http://localhost:8080/movies/{movieId}/list/true
INSERT INTO user_movie_list (user_id, movie_id) VALUES (?, ?);

-- Purpose: Removes a movie from the user's movie list.
-- URL Path: Movie_Review/src/main/java/movie/review/app/services/MovieService.java
-- URL Path: http://localhost:8080/movies/{movieId}/list/false
DELETE FROM user_movie_list WHERE user_id = ? AND movie_id = ?;

-- Purpose: Checks if a specific movie is already bookmarked by the user.
-- URL Path: Movie_Review/src/main/java/movie/review/app/services/MovieService.java
-- URL Path: http://localhost:8080/movies/{movieId}/list/{isAdd}
SELECT * FROM user_movie_list WHERE user_id = ? AND movie_id = ?;

-- Purpose: Security check to verify that a review belongs to the logged-in user.
-- This is used to determine whether to show controls allowing modification or deletion of a specific review
-- and to prevent a user trying to modify other users' reviews by typing in the url. 
-- URL Path: Movie_Review/src/main/java/movie/review/app/services/MovieService.java
-- URL Path: http://localhost:8080/movies/{movieId}
-- URL Path: http://localhost:8080/movies/{movieId}/reviews/{reviewId}/edit
-- URL Path: http://localhost:8080/movies/{movieId}/reviews/{reviewId}/update
-- URL Path: http://localhost:8080/movies/{movieId}/reviews/{reviewId}/delete
SELECT COUNT(*) FROM review WHERE review_id = ? AND user_id = ?;

-- Purpose: Retrieves a specific review and the author's name for a detail or edit view.
-- URL Path: Movie_Review/src/main/java/movie/review/app/services/MovieService.java
-- URL Path: http://localhost:8080/movies/{movieId}
-- URL Path: http://localhost:8080/movies/{movieId}/reviews/{reviewId}/edit
SELECT r.*, CONCAT(u.firstName, ' ', u.lastName) as userName
FROM review r, user u
WHERE r.review_id = ?
  AND r.user_id = u.user_id;


-- 5. SEARCH & RECOMMENDATIONS

-- Purpose: General title search with popularity sorting.
-- URL Path: Movie_Review/src/main/java/movie/review/app/services/SearchService.java
-- URL Path: http://localhost:8080/search?query=
SELECT m.movie_id, m.title,
       IFNULL(m.poster, 'https://via.placeholder.com/500x750?text=No+Poster') AS poster,
       IFNULL(m.tagline, '') AS tagline,
       IF(uml.user_id IS NULL, 0, 1) AS isListed
FROM Movie m
         LEFT JOIN user_movie_list uml ON m.movie_id = uml.movie_id AND uml.user_id = ?
WHERE LOWER(m.title) LIKE CONCAT('%', LOWER(?),'%')
ORDER BY m.popularity DESC
    LIMIT 50;

-- Purpose: Dynamic filtering for recommendations (Minimum Rating).
-- URL Path: Movie_Review/src/main/java/movie/review/app/services/RecommendationService.java
-- URL Path: http://localhost:8080/recommendations?minRating=4
SELECT r.movie_id FROM review r
GROUP BY r.movie_id
HAVING AVG(r.rating) >= ?;

-- Purpose: Dynamic filtering for recommendations (Genre Selection).
-- URL Path: Movie_Review/src/main/java/movie/review/app/services/RecommendationService.java
-- URL Path: http://localhost:8080/recommendations?genre=action&genre=sci-fi
SELECT mg.movie_id FROM movie_genre mg
                            JOIN genre g ON mg.genre_id = g.genre_id
WHERE g.name IN (?);

-- Purpose: Filters movies by a minimum average user rating.
-- URL Path: Movie_Review/src/main/java/movie/review/app/services/RecommendationService.java
-- URL Path: http://localhost:8080/recommendations?minRating=4
SELECT r.movie_id FROM review r
GROUP BY r.movie_id
HAVING AVG(r.rating) >= 4.0;

-- Purpose: Filters movies to include only those belonging to specific selected genres.
-- URL Path: Movie_Review/src/main/java/movie/review/app/services/RecommendationService.java
-- URL Path: http://localhost:8080/recommendations?genre=action&genre=sci-fi
SELECT mg.movie_id FROM movie_genre mg
                            JOIN genre g ON mg.genre_id = g.genre_id
WHERE g.name IN ('Sci-Fi', 'Action');

-- Purpose: Filters movies produced by a specific company using partial name matching.
-- URL Path: Movie_Review/src/main/java/movie/review/app/services/RecommendationService.java
-- URL Path: http://localhost:8080/recommendations?prodCompany=warner+bros
SELECT mc.movie_id FROM movie_company mc
                            JOIN production_company pc ON mc.company_id = pc.company_id
WHERE pc.name LIKE '%Warner Bros%';

-- Purpose: Retrieves the paginated list of movies based on the search/filter criteria.
--          The 'queryFragment' contains the dynamic WHERE and JOIN clauses.
-- URL Path: Movie_Review/src/main/java/movie/review/app/services/RecommendationService.java
-- URL Path: http://localhost:8080/recommendations?sortBy=rating&sortByOrder=desc&minRating=4.0&genre=action&genre=sci-fi&prodCompany=Warner+Bros
SELECT m.movie_id, m.title, m.poster, m.tagline,
       (SELECT COUNT(*) = 1 FROM user_movie_list uml
        WHERE uml.user_id = ? AND uml.movie_id = m.movie_id) as isListed
FROM movie m
WHERE 1=1
ORDER BY m.popularity DESC
    LIMIT 12 OFFSET 0;

-- Purpose: Counts the total number of movies that match the current search/filter
--          criteria to calculate total pages for pagination.
-- URL Path: Movie_Review/src/main/java/movie/review/app/services/RecommendationService.java
-- URL Path: http://localhost:8080/recommendations?sortBy=rating&sortByOrder=desc&minRating=4.0&genre=action&genre=sci-fi&prodCompany=Warner+Bros
SELECT COUNT(*) FROM movie m WHERE 1=1;


-- 6. PROFILE & BOOKMARKS DISPLAY

-- Purpose: Retrieves all movies currently bookmarked by a user.
-- URL Path: Movie_Review/src/main/java/movie/review/app/services/ProfileService.java
-- URL Path: http://localhost:8080/profile 
-- URL Path: http://localhost:8080/profile/{userId}
SELECT m.movie_id, m.title, m.poster, m.tagline
FROM movie m, user_movie_list uml
WHERE uml.user_id = ? AND m.movie_id = uml.movie_id
GROUP BY m.movie_id
ORDER BY AVG(m.popularity) DESC;

-- Purpose: Fetches all reviews written by a user for their profile page.
-- URL Path: Movie_Review/src/main/java/movie/review/app/services/ProfileService.java
-- URL Path: http://localhost:8080/profile 
-- URL Path: http://localhost:8080/profile/{userId}
SELECT r.*, DATE_FORMAT(postDate, '%b %d, %Y') as post_date,
       CONCAT(u.firstName, ' ', u.lastName) as userName, u.user_id user_id,
       m.title, m.poster, m.tagline
FROM review r, user u, movie m
WHERE r.user_id = u.user_id AND r.user_id = ? AND m.movie_id = r.movie_id;