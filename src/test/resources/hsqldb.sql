DROP TABLE star IF EXISTS;
DROP TABLE movies IF EXISTS;
CREATE TABLE star (id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY, firstname VARCHAR(20), lastname VARCHAR(20));
CREATE TABLE movies ( movieid INTEGER PRIMARY KEY, starid INTEGER, title VARCHAR(40)) ;
INSERT INTO star (id, firstname, lastname) VALUES (DEFAULT, 'Felix', 'the Cat');
INSERT INTO movies (movieid, starid, title) VALUES (IDENTITY(), 10, 'Felix in Hollywood');
