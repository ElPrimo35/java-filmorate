package ru.yandex.practicum.filmorate;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.time.Month;

public class FilmControllerTest {
    private final FilmStorage filmStorage = new InMemoryFilmStorage();
    private final UserStorage userStorage = new InMemoryUserStorage();
    private final FilmService filmService = new FilmService(filmStorage, userStorage);

    private final FilmController filmController = new FilmController(filmService);
    private static Validator validator;

    @BeforeAll
    public static void setUp() {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Test
    public void emptyUserTest() {
        Film film = new Film();

        Assertions.assertThrows(RuntimeException.class, () -> filmController.createFilm(film));
    }

    @Test
    public void emptyNameTest() {
        Film film = new Film();
        film.setDescription("Adventure of two dump friends");
        film.setName("");
        film.setReleaseDate(LocalDate.of(1994, Month.DECEMBER, 16));
        film.setDuration(106);

        Assertions.assertFalse(validator.validate(film).isEmpty());
    }

    @Test
    public void descriptionLengthTest() {
        Film film = new Film();
        film.setName("Dumb and Dumber");
        film.setDescription("Lloyd Christmas and Harry Dunne, two dimwitted young men, are best friends and roommates" +
                " living in Providence, Rhode Island. Lloyd, a chip-toothed limousine driver, falls in love with Mary" +
                " Swanson, a woman he is driving to the airport. She leaves a briefcase in the terminal. Lloyd attempts" +
                " to return it, unaware that it contains ransom money for her kidnapped husband, Bobby, and that she" +
                " left it for her husband's captors, Joe \"Mental\" Mentalino and J. P. Shay.");
        film.setReleaseDate(LocalDate.of(1994, Month.DECEMBER, 16));
        film.setDuration(106);
        Assertions.assertFalse(validator.validate(film).isEmpty());
    }

    @Test
    public void emailTest() {
        Film film = new Film();
        film.setName("Dumb and Dumber");
        film.setDescription("Adventure of two dump friends");
        film.setReleaseDate(LocalDate.of(1895, Month.DECEMBER, 27));
        film.setDuration(106);
        Assertions.assertThrows(ValidationException.class, () -> filmController.createFilm(film));
    }

    @Test
    public void futureTimeTest() {
        Film film = new Film();
        film.setName("Dumb and Dumber");
        film.setDescription("Adventure of two dump friends");
        film.setReleaseDate(LocalDate.of(1994, Month.DECEMBER, 16));
        film.setDuration(0);
        Assertions.assertFalse(validator.validate(film).isEmpty());
    }
}
