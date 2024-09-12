package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;

import java.util.Map;

@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler(ValidationException.class)
    public Map<String, String> handleValidationException(final ValidationException e) {
        return Map.of("error", "Ошибка валидации",
                "errorMessage", e.getMessage()
        );
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNoSuchElementException(final NotFoundException e) {
        return new ResponseEntity<>(Map.of("error:", "объект не найден",
                "errorResponse", e.getMessage()
        ), HttpStatus.NOT_FOUND);
    }
}
