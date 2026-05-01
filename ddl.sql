CREATE DATABASE IF NOT EXISTS movie_review_db;
USE movie_review_db;

CREATE TABLE Movie (
    movie_id INT PRIMARY KEY,
    title VARCHAR(300) NOT NULL,
    vote_average DECIMAL(4,3),
    vote_count INT,
    release_date DATE,
    revenue BIGINT,
    budget BIGINT,
    homepage VARCHAR(200),
    runtime INT,
    poster VARCHAR(200),
    original_language VARCHAR(2),
    original_title VARCHAR(500),
    overview TEXT,
    tagline TEXT,
    popularity DECIMAL(10,3)
);

CREATE TABLE Genre (
    genre_id INT PRIMARY KEY,
    name VARCHAR(100) NOT NULL
);

CREATE TABLE Movie_Genre (
    movie_id INT,
    genre_id INT,
    PRIMARY KEY (movie_id, genre_id),
    FOREIGN KEY (movie_id) REFERENCES Movie(movie_id),
    FOREIGN KEY (genre_id) REFERENCES Genre(genre_id)
);

CREATE TABLE Production_Company (
    company_id INT PRIMARY KEY,
    name VARCHAR(500) NOT NULL
);

CREATE TABLE Movie_Company (
    movie_id INT,
    company_id INT,
    PRIMARY KEY (movie_id, company_id),
    FOREIGN KEY (movie_id) REFERENCES Movie(movie_id),
    FOREIGN KEY (company_id) REFERENCES Production_Company(company_id)
);

CREATE TABLE User (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    firstName VARCHAR(50),
    lastName VARCHAR(50)
);

CREATE TABLE Review (
    review_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    movie_id INT NOT NULL,
    content TEXT,
    postDate DATE,
    rating DECIMAL(2,1) CHECK (rating >= 1.0 AND rating <= 5.0),
    FOREIGN KEY (user_id) REFERENCES User(user_id),
    FOREIGN KEY (movie_id) REFERENCES Movie(movie_id)
);

CREATE TABLE User_Movie_List (
    user_id INT,
    movie_id INT,
    PRIMARY KEY (user_id, movie_id),
    FOREIGN KEY (user_id) REFERENCES User(user_id),
    FOREIGN KEY (movie_id) REFERENCES Movie(movie_id)
);

-- Indexes for performance optimization
CREATE INDEX idx_movie_release_date ON Movie(release_date);
CREATE INDEX idx_movie_popularity ON Movie(popularity);