package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.relational.core.sql.In;


@Data
public class Genre {
    private Integer id;
    private String name;
}
