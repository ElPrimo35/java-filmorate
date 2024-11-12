package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
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

    private Film createTestFilm() {
        Mpa mpa = new Mpa();
        mpa.setId(1);
        Film film = new Film();
        film.setId(1);
        film.setName("Dumb and dumber");
        film.setDescription("Loyd Christmas and Harry Dunne");
        film.setReleaseDate(LocalDate.of(1994, Month.DECEMBER, 16));
        film.setDuration(106);
        film.setMpa(mpa);
        return filmStorage.createFilm(film);
    }

    private User createTestUser() {
        User user = new User();
        user.setId(5);
        user.setEmail("ElPrimo35@Gmail.com");
        user.setLogin("ElPrimo");
        user.setName("Primat");
        user.setBirthday(LocalDate.of(2003, Month.NOVEMBER, 24));
        return userStorage.createUser(user);
    }

    @Test
    public void testGetUserById() {

        User user = createTestUser();
        Optional<User> userOptional = userStorage.getUserById(user.getId());

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user1 ->
                        assertThat(user1).hasFieldOrPropertyWithValue("id", user.getId())
                );
    }

    @Test
    public void testGetFilmById() {

        Film film = createTestFilm();
        Optional<Film> filmOptional = filmStorage.getFilmById(film.getId());

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film1 ->
                        assertThat(film1).hasFieldOrPropertyWithValue("id", film.getId())
                );
    }


    @Test
    public void testCreateFilm() {

        Film film = createTestFilm();
        Film film1 = filmStorage.createFilm(film);
        Assertions.assertFalse(filmStorage.getFilmById(film1.getId()).isEmpty());
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
        Film film1 = createTestFilm();
        film1.setDescription("Loyd Christmas and Harry Dunne1");
        filmStorage.updateFilm(film1);
        Assertions.assertEquals("Loyd Christmas and Harry Dunne1", filmStorage.getFilmById(film1.getId()).get().getDescription());
    }


    @Test
    public void testCreateUser() {
        User user = createTestUser();
        Assertions.assertFalse(userStorage.getUserById(user.getId()).isEmpty());
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
        User user = createTestUser();
        user.setEmail("ElPrimo35@Gmail.com1");
        userStorage.updateUser(user);
        Assertions.assertEquals("ElPrimo35@Gmail.com1", userStorage.getUserById(user.getId()).get().getEmail());
    }
}
