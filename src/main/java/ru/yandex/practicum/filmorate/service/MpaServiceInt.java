package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

public interface MpaServiceInt {
    List<Mpa> getAllMpa();

    Mpa getMpa(Integer id);
}
