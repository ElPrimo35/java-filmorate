package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({UserDbStorage.class, FilmDbStorage.class})
class FilmoRateApplicationTests {
    private final UserDbStorage userStorage;
    private final FilmDbStorage filmStorage;

    @Test
    public void testGetUserById() {

        Optional<User> userOptional = userStorage.getUserById(1);

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 1)
                );
    }

    @Test
    public void testGetFilmById() {

        Optional<Film> filmOptional = filmStorage.getFilmById(1);

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 1)
                );
    }


    @Test
    public void testCreateFilm() {
        Film film = new Film();
        film.setId(3);
        film.setName("Dumb and dumber");
        film.setDescription("Loyd Christmas and Harry Dunne");
        film.setReleaseDate(LocalDate.of(1994, Month.DECEMBER, 16));
        film.setDuration(106);
        film.setGenre(1);
        film.setMpa(1);
        filmStorage.createFilm(film);
        Assertions.assertFalse(filmStorage.getFilmById(3).isEmpty());
    }


    @Test
    public void testGetFilmsList() {
        List<Film> filmsList = filmStorage.getFilmsList();
        Film notExistFilm = new Film();
        notExistFilm.setId(1000);
        boolean flag = true;
        for (int i = 1; i < filmsList.size(); i++) {
            if (!filmsList.contains(filmStorage.getFilmById(i).orElse(notExistFilm))) {
                flag = false;
                break;
            }
        }
        Assertions.assertTrue(flag);
    }

    @Test
    public void testUpdateFilm() {
        Film film = new Film();
        film.setId(1);
        film.setName("Dumb and dumber");
        film.setDescription("Loyd Christmas and Harry Dunne");
        film.setReleaseDate(LocalDate.of(1994, Month.DECEMBER, 16));
        film.setDuration(106);
        film.setGenre(1);
        film.setMpa(1);
        filmStorage.updateFilm(film);
        Assertions.assertEquals("Loyd Christmas and Harry Dunne", filmStorage.getFilmById(1).get().getDescription());
    }

    @Test
    public void testCreateUser() {
        User user = new User();
        user.setId(5);
        user.setEmail("ElPrimo35@Gmail.com");
        user.setLogin("ElPrimo");
        user.setName("Primat");
        user.setBirthday(LocalDate.of(2003, Month.NOVEMBER, 24));
        userStorage.createUser(user);
        Assertions.assertFalse(userStorage.getUserById(5).isEmpty());
    }

    @Test
    public void testGetUserList() {
        List<User> usersList = userStorage.getUsersList();
        User notExistsUser = new User();
        notExistsUser.setId(1000);
        boolean flag = true;
        for (int i = 1; i < usersList.size(); i++) {
            if (!usersList.contains(userStorage.getUserById(i).orElse(notExistsUser))) {
                flag = false;
                break;
            }
        }
        Assertions.assertTrue(flag);
    }

    @Test
    public void testUpdateUser() {
        User user = new User();
        user.setId(1);
        user.setEmail("ElPrimo35@Gmail.com");
        user.setLogin("ElPrimo");
        user.setName("Primat");
        user.setBirthday(LocalDate.of(2003, Month.NOVEMBER, 24));
        userStorage.updateUser(user);
        Assertions.assertEquals("ElPrimo35@Gmail.com", userStorage.getUserById(1).get().getEmail());
    }
}
