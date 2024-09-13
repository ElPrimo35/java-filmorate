package ru.yandex.practicum.filmorate.response;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ErrorResponse {
    private final String error;
    private final String errorMessage;
}
