package ru.yandex.practicum.filmorate.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ErrorResponse {
    private final String error;
    private final String errorMessage;
}
