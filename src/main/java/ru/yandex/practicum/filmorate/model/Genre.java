package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Size;
import lombok.Data;


@Data
public class Genre {
    private Integer id;
    private String name;
}
