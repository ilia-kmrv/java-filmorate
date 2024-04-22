package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Data
@Builder
public class User {

    private long id;

    @NotBlank(message = "email пользователя не может быть пустой или null")
    @Email(message = "некорректный формат email")
    private String email;

    @NotBlank(message = "логин не может быть пустым или null")
    @Pattern(regexp = "^\\S+\\S$", message = "логин не может содержать пробелы") // должен проверять на наличие пробелов
    private String login;

    private String name;

    @NotNull
    @Past(message = "дата рождения не может быть в будущем")
    private LocalDate birthday;

    public Map<String,Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("email", email);
        values.put("login", login);
        values.put("name", name);
        values.put("birthday", birthday);
        return values;
    }
}
