package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Film.
 */
@Data
public class Film {
    private int id;
    @NotBlank(message = "название фильма не должно быть пустым")
    private String name;
    @Size(max = 200, message = "Описание должно быть короче двухсот символов!")
    private String description;
    @NotNull
    @Past
    private LocalDate releaseDate;
    @Positive(message = "Продолжительность фильма должна быть положительной")
    private int duration;
    private Set<Integer> usersLikedId = new HashSet<>();
    private int likesCount;
}
