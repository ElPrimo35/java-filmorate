package ru.yandex.practicum.filmorate;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.time.Month;

public class UserControllerTest {
    private final UserStorage userStorage = new InMemoryUserStorage();

    private final UserService userService = new UserService(userStorage);
    private final UserController userController = new UserController(userService);
    private static Validator validator;

    @BeforeAll
    public static void setUp() {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Test
    public void emptyUserTest() {
        User user = new User();

        Assertions.assertFalse(validator.validate(user).isEmpty());
    }

    @Test
    public void emptyNameTest() {
        User user = new User();
        user.setEmail("ElPrimo35@Gmail.com");
        user.setLogin("ElPrimo");
        user.setName("");
        user.setBirthday(LocalDate.of(2003, Month.NOVEMBER, 24));
        User user1 = userController.createUser(user);
        Assertions.assertEquals(user1.getName(), user1.getLogin());
    }

    @Test
    public void loginTest() {
        User user = new User();
        user.setEmail("ElPrimo35@gmail.com");
        user.setLogin("");
        user.setName("Misha");
        user.setBirthday(LocalDate.of(2003, Month.NOVEMBER, 24));
        Assertions.assertFalse(validator.validate(user).isEmpty());


        User user1 = new User();

        user.setEmail("ElPrimo35@gmail.com");
        user.setLogin("El Primo");
        user.setName("Misha");
        user.setBirthday(LocalDate.of(2003, Month.NOVEMBER, 24));
        Assertions.assertFalse(validator.validate(user1).isEmpty());

    }

    @Test
    public void emailTest() {
        User user = new User();
        user.setEmail("");
        user.setLogin("ElPrimo");
        user.setName("Misha");
        user.setBirthday(LocalDate.of(2003, Month.NOVEMBER, 24));
        Assertions.assertFalse(validator.validate(user).isEmpty());


        User user1 = new User();
        user.setEmail("ElPrimo35gmail.com");
        user.setLogin("ElPrimo");
        user.setName("Misha");
        user.setBirthday(LocalDate.of(2003, Month.NOVEMBER, 24));
        Assertions.assertFalse(validator.validate(user1).isEmpty());

    }

    @Test
    public void futureTimeTest() {
        User user = new User();
        user.setEmail("ElPrimo35@Gmail.com");
        user.setLogin("ElPrimo");
        user.setName("Misha");
        user.setBirthday(LocalDate.of(3000, Month.NOVEMBER, 24));
        Assertions.assertFalse(validator.validate(user).isEmpty());
    }
}
