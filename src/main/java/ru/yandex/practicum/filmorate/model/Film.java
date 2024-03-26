package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.validator.AfterDate;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Film.
 */

@Data
@Builder
public class Film {

    public static final String FIRST_FILM_DATE = "1895-12-28";

    private long id;

    private final Set<Long> likes = new HashSet<>();

    @NotBlank(message = "Название должно содержать как минимум один непустой символ и не может быть null")
    private String name;

    @Size(max = 200, message = "Описание может быть длиной до 200 символов")
    private String description;


    @NotNull
    @AfterDate(FIRST_FILM_DATE)
    private LocalDate releaseDate;

    @Positive
    private int duration;

}
