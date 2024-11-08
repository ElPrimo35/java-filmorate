INSERT INTO FRIENDSTATUS ("id", "status")
VALUES (1, 'confirmed');

INSERT INTO FRIENDSTATUS ("id", "status")
VALUES (2, 'unconfirmed');

INSERT INTO MPA ("id", "MPA")
VALUES (1, 'ExampleMPA');

INSERT INTO FILMGENRES ("id", "name")
VALUES (1, 'ExampleGenre');

INSERT INTO FILMS ("id", "name", "description", "releaseDate", "duration", "genre", "MPA")
VALUES (2, 'Dumb and dumber 2', 'Two dumb friends together again', '2014-11-14', 104, 1, 1);

INSERT INTO FILMS ("id", "name", "description", "releaseDate", "duration", "genre", "MPA")
VALUES (1, 'Dumb and dumber', 'Two dumb friends', '1994-12-16', 106, 1, 1);

INSERT INTO USERS ("id", "email", "login", "name", "birthday")
VALUES (1, 'email@1', 'login', 'name', '2000-12-12');

INSERT INTO USERS ("id", "email", "login", "name", "birthday")
VALUES (2, 'email@2', 'login', 'name', '2000-12-12');

INSERT INTO USERSFILMS ("userId", "filmId")
VALUES (1, 2);

INSERT INTO USERSFILMS ("userId", "filmId")
VALUES (1, 1);

INSERT INTO USERFRIENDS ("userId", "friendId", "status")
VALUES (1, 2, 1);