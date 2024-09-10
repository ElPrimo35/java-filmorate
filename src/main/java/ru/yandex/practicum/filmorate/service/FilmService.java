package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.time.Month;
import java.util.Comparator;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class FilmService implements FilmServiceInt {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final Comparator<Film> comparator = new Comparator<Film>() {
        @Override
        public int compare(Film o1, Film o2) {
            return Integer.compare(o2.getLikesCount(), o1.getLikesCount());
        }
    };

    @Override
    public Film createFilm(Film film) {
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, Month.DECEMBER, 28))) {
            throw new ValidationException("Дата релиза фильма не может быть настолько ранней");
        }
        return filmStorage.createFilm(film);
    }

    @Override
    public void likeFilm(int id, int userId) {
        filmStorage.getFilmById(id).orElseThrow(() -> new NotFoundException("Фильм с id: " + id + " не найден"));
        userStorage.getUserById(userId).orElseThrow(() -> new NotFoundException("Пользователь с id: " + userId + " не найден"));
        Film film = filmStorage.getFilmsList().get(id - 1);
        int likes = film.getLikesCount();
        film.setLikesCount(++likes);
        film.getUsersLikedId().add(userId);
    }

    @Override
    public void removeLike(int id, int userId) {
        filmStorage.getFilmById(id).orElseThrow(() -> new NotFoundException("Фильм с id: " + id + " не найден"));
        userStorage.getUserById(userId).orElseThrow(() -> new NotFoundException("Пользователь с id: " + userId + " не найден"));
        Film film = filmStorage.getFilmsList().get(id - 1);
        int likes = film.getLikesCount();
        film.setLikesCount(--likes);
        film.getUsersLikedId().remove(userId - 1);
    }

    @Override
    public List<Film> getPopularFilms(Integer count) {
        List<Film> films = filmStorage.getFilmsList();
        films.sort(comparator);
        if (count == null || count > films.size()) {
            count = Math.min(films.size(), 10);
        }
        return films.subList(0, count);
    }


    @Override
    public List<Film> getFilmsList() {
        return filmStorage.getFilmsList();
    }

    @Override
    public ResponseEntity<Film> updateFilm(Film film) {
        try {
            filmStorage.updateFilm(film);
            return new ResponseEntity<>(film, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(film, HttpStatusCode.valueOf(404));
        }
    }
}
