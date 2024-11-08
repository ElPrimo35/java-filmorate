DROP TABLE IF EXISTS filmGenres CASCADE;
DROP TABLE IF EXISTS MPA CASCADE;
DROP TABLE IF EXISTS films CASCADE;
DROP TABLE IF EXISTS friendStatus CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS userFriends CASCADE;
DROP TABLE IF EXISTS usersFilms CASCADE;

CREATE TABLE IF NOT EXISTS filmGenres (
  "id" integer PRIMARY KEY,
  "name" varchar(15)
);

CREATE TABLE IF NOT EXISTS MPA (
  "id" integer PRIMARY KEY,
  "MPA" varchar(20) NOT NULL
);



CREATE TABLE IF NOT EXISTS films (
  "id" integer PRIMARY KEY,
  "name" varchar NOT NULL,
  "description" varchar(200),
  "releaseDate" date,
  "duration" integer,
  "genre" integer,
  "MPA" integer NOT NULL,
  FOREIGN KEY ("MPA") REFERENCES MPA("id"),
  FOREIGN KEY ("genre") REFERENCES filmGenres("id")
);

CREATE TABLE IF NOT EXISTS friendStatus (
  "id" integer PRIMARY KEY,
  "status" varchar(20)
);

CREATE TABLE IF NOT EXISTS users (
  "id" integer PRIMARY KEY,
  "email" varchar(40) NOT NULL,
  "login" varchar(30) NOT NULL,
  "name" varchar(15) NOT NULL,
  "birthday" date NOT NULL
);

CREATE TABLE IF NOT EXISTS userFriends (
  "userId" integer,
  "friendId" integer,
  "status" integer NOT NULL,
  PRIMARY KEY ("userId", "friendId"),
  FOREIGN KEY ("status") REFERENCES friendStatus("id"),
  FOREIGN KEY ("userId") REFERENCES users("id"),
  FOREIGN KEY ("friendId") REFERENCES users("id")
);

CREATE TABLE IF NOT EXISTS usersFilms (
  "userId" integer,
  "filmId" integer,
  PRIMARY KEY ("userId", "filmId"),
  FOREIGN KEY ("userId") REFERENCES users("id"),
  FOREIGN KEY ("filmId") REFERENCES films("id")
);