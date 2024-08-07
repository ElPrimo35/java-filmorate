package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final Map<Integer, Film> films = new HashMap<>();
    private int idCounter = 1;

    @PostMapping
    public Film createFilm(@RequestBody Film film) {
        log.info("Пришёл запрос на создание фильма с названием " + film.getName());
        validate(film);
        film.setId(idCounter++);
        films.put(film.getId(), film);
        return film;
    }

    @GetMapping
    public List<Film> getFilmsList() {
        return new ArrayList<>(films.values());
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        log.info("Пришёл запрос на обновление данных фильма с названием " + film.getName());
        validate(film);
        if (films.containsKey(film.getId())) {
            films.get(film.getId()).setName(film.getName());
            films.get(film.getId()).setDescription(film.getDescription());
            films.get(film.getId()).setReleaseDate(film.getReleaseDate());
            films.get(film.getId()).setDuration(film.getDuration());
            return films.get(film.getId());
        } else {
            throw new ValidationException("Произошла ошибка");
        }

    }

    private void validate(Film film) {
        if (film.getName() == null || film.getName().isEmpty()) {
            throw new ValidationException("название фильма не должно быть пустым");
        }
        if (film.getDescription().length() > 200) {
            throw new ValidationException("Длина описания не должна превышать 200 символов");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, Month.DECEMBER, 28))) {
            throw new ValidationException("Дата релиза фильма не может быть настолько ранней");
        }
        if (film.getDuration() <= 0) {
            throw new ValidationException("Продолжительность фильма должна быть положительной");
        }
    }
}
