package ru.yandex.practicum.filmorate.model;

import lombok.*;
import ru.yandex.practicum.filmorate.validator.AfterDate;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.*;

@Data
@Builder
@AllArgsConstructor
@Getter
@Setter
public class Film {

    public static final String FIRST_FILM_DATE = "1895-12-28";

    private long id;

    @Builder.Default
    private Set<Long> likes = new HashSet<>();

    @NotBlank(message = "Название должно содержать как минимум один непустой символ и не может быть null")
    private String name;

    @Size(max = 200, message = "Описание может быть длиной до 200 символов")
    private String description;


    @NotNull
    @AfterDate(FIRST_FILM_DATE)
    private LocalDate releaseDate;

    @Positive
    private int duration;

    @NotNull
    private MpaRating mpa;
    @Builder.Default
    private LinkedHashSet<Genre> genres = new LinkedHashSet<>();

    public Map<String,Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("name", name);
        values.put("description", description);
        values.put("release_date", releaseDate);
        values.put("duration", duration);
        values.put("mpa_rating", mpa.getId());
        values.put("genres", genres);
        return values;
    }
}
