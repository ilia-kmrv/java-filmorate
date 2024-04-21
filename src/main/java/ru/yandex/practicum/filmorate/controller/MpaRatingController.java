package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.service.MpaRatingService;

import javax.validation.Valid;
import java.util.Collection;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/mpa")
public class MpaRatingController {

    private final MpaRatingService mpaRatingService;

    @PostMapping
    public MpaRating postUser(@Valid @RequestBody MpaRating mpaRating) {
        log.info("Обработан POST MpaRating запрос.");
        return mpaRatingService.addMpaRating(mpaRating);
    }

    @GetMapping("/{mpaRatingId}")
    public MpaRating getMpaRating(@PathVariable long mpaRatingId) {
        log.info("Обработан GET MpaRating {} запрос.", mpaRatingId);
        return mpaRatingService.getMpaRating(mpaRatingId);
    }

    @PutMapping
    public MpaRating putMpaRating(@Valid @RequestBody MpaRating mpaRating) {
        log.info("Обработан PUT MpaRating запрос.");
        return mpaRatingService.updateMpaRating(mpaRating);
    }

    @DeleteMapping("/{userId}")
    public void deleteMpaRating(@PathVariable long mpaRatingId) {
        log.info("Обработан DELETE MpaRating {} запрос.", mpaRatingId);
        mpaRatingService.deleteMpaRating(mpaRatingId);
    }

    @GetMapping
    public Collection<MpaRating> getAllMpaRatings() {
        log.info("Обработан GET MpaRatings запрос");
        return mpaRatingService.getAllMpaRatings();
    }
}
