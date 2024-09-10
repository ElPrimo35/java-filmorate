package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();
    private int idCounter = 1;
    @Override
    public Film createFilm(Film film) {

        film.setId(idCounter++);
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public List<Film> getFilmsList() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Optional<Film> getFilmById(int id) {
        return Optional.ofNullable(films.get(id));
    }

    @Override
    public Film updateFilm(Film film) {
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
}
