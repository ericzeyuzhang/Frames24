CREATE PROCEDURE insert_genre(
    IN v_name VARCHAR(32), 
    OUT is_exist BOOLEAN, 
    OUT v_id INT
    )
BEGIN
    SET is_exist = 1;
    SELECT id FROM genres WHERE name LIKE v_name LIMIT 1 INTO v_id;
    IF v_id IS NULL THEN 
        INSERT INTO genres (name) VALUES(v_name);
        SET v_id = LAST_INSERT_ID();  
        SET is_exist = 0; 
    END IF;
END$$

SELECT insert_genre('tytt')$$

CREATE PROCEDURE insert_star(
    IN v_name VARCHAR(100), 
    IN v_birthYear INT, 
    OUT is_exist BOOLEAN,
    OUT v_id VARCHAR(10) 
    ) 
BEGIN
    SET is_exist = 1;
    IF v_birthYear IS NULL THEN
        SELECT id FROM stars WHERE name LIKE v_name AND birthYear IS NULL LIMIT 1 INTO v_id;
    ELSE
        SELECT id FROM stars WHERE name LIKE v_name AND birthYear = v_birthYear LIMIT 1 INTO v_id;
    END IF;
    IF v_id IS NULL THEN 
        SELECT MAX(id) FROM stars INTO v_id; 
        SET v_id = CONCAT("nm", LPAD(CAST(CAST(SUBSTRING(v_id, 3, 9) AS UNSIGNED) + 1 AS CHAR(30)), 7, '0'));
        INSERT INTO stars VALUES(v_id, v_name, v_birthYear);
        SET is_exist = 0;
    END IF;
END$$

SELECT insert_star('TEST', NULL)$$

CREATE PROCEDURE insert_movie(
    IN v_title VARCHAR(100), 
    IN v_year INT, 
    IN v_director VARCHAR(100), 
    OUT is_exist BOOLEAN, 
    OUT v_id VARCHAR(10) 
    )  
BEGIN
    SET is_exist = 1;
    SELECT id FROM movies WHERE title LIKE v_title AND year = v_year AND director LIKE v_director LIMIT 1 INTO v_id;
    IF v_id IS NULL THEN 
        SELECT MAX(id) FROM movies INTO v_id; 
        SET v_id = CONCAT("tt", LPAD(CAST(CAST(SUBSTRING(v_id, 3, 9) AS UNSIGNED) + 1 AS CHAR(30)), 7, '0'));
        INSERT INTO movies VALUES(v_id, v_title, v_year, v_director);
        SET is_exist = 0;
    END IF;
END$$
SELECT insert_movie('test', 2007, 'mani ratnam')$$


CREATE PROCEDURE link_genre(
    IN v_movieId VARCHAR(10), 
    IN v_genre VARCHAR(32), 
    OUT status_code INT 
    )
BEGIN
    DECLARE v_genreId INT;
    DECLARE genre_status BOOLEAN;
    CALL insert_genre(v_genre, genre_status, v_genreId);
    IF (SELECT COUNT(id) FROM movies WHERE id = v_movieId) = 0 THEN 
        SET status_code = 1;
    ELSEIF (SELECT COUNT(genreId) FROM genres_in_movies WHERE genreId = v_genreId AND movieId = v_movieId) > 0 THEN 
        SET status_code = 2;
    ELSE 
        SET status_code = 0;
        INSERT INTO genres_in_movies VALUES (v_genreId, v_movieId);
    END IF;
END$$

CREATE PROCEDURE update_rating(
    IN v_movieId VARCHAR(10), 
    IN v_rating FLOAT, 
    IN v_rating_votes INT, 
    OUT status_code INT
    )
BEGIN
    IF (SELECT COUNT(*) FROM movies WHERE id = v_movieId) = 0 THEN 
        SET status_code = 1;
    ELSE 
        SET status_code = 0;
        IF (SELECT COUNT(*) FROM ratings WHERE movieId = v_movieId) = 0 THEN 
            INSERT INTO ratings VALUES (v_movieId, v_rating, v_rating_votes);
        ELSE 
            UPDATE ratings SET rating = v_rating, numVotes = v_rating_votes WHERE movieId = v_movieId;
        END IF;
    END IF;
END$$ 


CREATE PROCEDURE link_star(
    IN v_movieId VARCHAR(10), 
    IN v_star VARCHAR(32), 
    IN v_birthYear INT,
    OUT status_code INT 
    )
BEGIN
    DECLARE v_starId VARCHAR(10);
    DECLARE star_status BOOLEAN;
    CALL insert_star(v_star, v_birthYear, star_status, v_starId);
    IF (SELECT COUNT(id) FROM movies WHERE id = v_movieId) = 0 THEN 
        SET status_code = 1;
    ELSEIF (SELECT COUNT(starId) FROM stars_in_movies WHERE starId = v_starId AND movieId = v_movieId) > 0 THEN 
        SET status_code = 2;
    ELSE 
        SET status_code = 0;
        INSERT INTO stars_in_movies VALUES (v_starId, v_movieId);
    END IF;
END$$


CREATE PROCEDURE insert_product(
    IN v_title VARCHAR(100), 
    IN v_year INT, 
    IN v_director VARCHAR(100), 
    IN v_star VARCHAR(100), 
    IN v_genre VARCHAR(32), 
    OUT v_movieId VARCHAR(10), 
    OUT star_status INT, 
    OUT genre_status INT 
    )
BEGIN
    -- DECLARE v_movieId VARCHAR(10);
    DECLARE v_starId VARCHAR(10);
    DECLARE v_genreId INT;
    
    SET star_status = 0;
    SET genre_status = 0;

    SELECT insert_movie(v_title, v_year, v_director) INTO v_movieId;
    SELECT insert_star(v_star, NULL) INTO v_starId;
    SELECT insert_genre(v_genre) INTO v_genreId;

    IF (SELECT COUNT(genreId) FROM genres_in_movies WHERE genreId = v_genreId AND movieId = v_movieId) = 0 THEN 
        INSERT INTO genres_in_movies VALUES (v_genreId, v_movieId);
        SET genre_status = 1;
    END IF;

    IF (SELECT COUNT(starId) FROM stars_in_movies WHERE starId = v_starId AND movieId = v_movieId) = 0 THEN 
        INSERT INTO stars_in_movies VALUES (v_starId, v_movieId);
        SET star_status = 1;
    END IF;

END$$

call insert_product('test',9999,'test','test1','test1')$$

