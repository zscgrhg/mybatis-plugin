DROP TABLE admin IF EXISTS;
DROP TABLE star IF EXISTS;
DROP TABLE movies IF EXISTS;

CREATE TABLE admin(id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY, name VARCHAR(20), pwd VARCHAR(20));
CREATE TABLE star (id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY, firstname VARCHAR(20), lastname VARCHAR(20));
CREATE TABLE movies (starid INTEGER, movieid INTEGER PRIMARY KEY, title VARCHAR(40)) ;

INSERT INTO admin(id, name, pwd) VALUES (1, 'tester', 'testpassword');
INSERT INTO star (id, firstname, lastname) VALUES (1, 'Felix', 'the Cat');
INSERT INTO star (id, firstname, lastname) VALUES (2, 'Alex', 'the Dog');
INSERT INTO movies (starid, movieid, title) VALUES (IDENTITY(), 10, 'Felix in Hollywood');