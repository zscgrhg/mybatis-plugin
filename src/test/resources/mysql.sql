DROP TABLE IF EXISTS star;
DROP TABLE IF EXISTS movies;

CREATE TABLE star (id INTEGER NOT NULL AUTO_INCREMENT, firstname VARCHAR(20) NOT NULL, lastname VARCHAR(20) NOT NULL, PRIMARY KEY (id));
CREATE TABLE movies (movieid INTEGER NOT NULL AUTO_INCREMENT, starid INTEGER, title VARCHAR(40), PRIMARY KEY(movieid)) ;
INSERT INTO star (id, firstname, lastname) VALUES (LAST_INSERT_ID(), 'Felix', 'the Cat');
INSERT INTO movies (starid, movieid, title) VALUES (LAST_INSERT_ID(), 10, 'Felix in Hollywood');
