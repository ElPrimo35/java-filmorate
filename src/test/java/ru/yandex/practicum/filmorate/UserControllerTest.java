package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.time.Month;

public class UserControllerTest {
    private final UserController userController = new UserController();

    @Test
    public void emptyUserTest() {
        User user = new User();

        Assertions.assertThrows(ValidationException.class, () -> userController.createUser(user));
    }

    @Test
    public void emptyNameTest() {
        User user = new User();
        user.setEmail("ElPrimo35@Gmail.com");
        user.setLogin("ElPrimo");
        user.setName("");
        user.setBirthday(LocalDate.of(2003, Month.NOVEMBER, 24));
        userController.createUser(user);
        Assertions.assertEquals(user.getName(), user.getLogin());
    }

    @Test
    public void loginTest() {
        User user = new User();
        user.setEmail("ElPrimo35@gmail.com");
        user.setLogin("");
        user.setName("Misha");
        user.setBirthday(LocalDate.of(2003, Month.NOVEMBER, 24));
        Assertions.assertThrows(ValidationException.class, () -> userController.createUser(user));

        User user1 = new User();

        user.setEmail("ElPrimo35@gmail.com");
        user.setLogin("El Primo");
        user.setName("Misha");
        user.setBirthday(LocalDate.of(2003, Month.NOVEMBER, 24));
        Assertions.assertThrows(ValidationException.class, () -> userController.createUser(user1));
    }

    @Test
    public void emailTest() {
        User user = new User();
        user.setEmail("");
        user.setLogin("ElPrimo");
        user.setName("Misha");
        user.setBirthday(LocalDate.of(2003, Month.NOVEMBER, 24));
        Assertions.assertThrows(ValidationException.class, () -> userController.createUser(user));

        User user1 = new User();
        user.setEmail("ElPrimo35gmail.com");
        user.setLogin("ElPrimo");
        user.setName("Misha");
        user.setBirthday(LocalDate.of(2003, Month.NOVEMBER, 24));
        Assertions.assertThrows(ValidationException.class, () -> userController.createUser(user1));
    }

    @Test
    public void futureTimeTest() {
        User user = new User();
        user.setEmail("ElPrimo35@Gmail.com");
        user.setLogin("ElPrimo");
        user.setName("Misha");
        user.setBirthday(LocalDate.of(3000, Month.NOVEMBER, 24));
        Assertions.assertThrows(ValidationException.class, () -> userController.createUser(user));
    }
}
