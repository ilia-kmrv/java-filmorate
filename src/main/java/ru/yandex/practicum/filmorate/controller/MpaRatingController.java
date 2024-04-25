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
    public MpaRating postMpaRating(@Valid @RequestBody MpaRating mpaRating) {
        log.debug("Обработан POST MpaRating запрос.");
        return mpaRatingService.addMpaRating(mpaRating);
    }

    @GetMapping("/{mpaRatingId}")
    public MpaRating getMpaRating(@PathVariable long mpaRatingId) {
        log.debug("Обработан GET MpaRating {} запрос.", mpaRatingId);
        return mpaRatingService.getMpaRating(mpaRatingId);
    }

    @PutMapping
    public MpaRating putMpaRating(@Valid @RequestBody MpaRating mpaRating) {
        log.debug("Обработан PUT MpaRating запрос.");
        return mpaRatingService.updateMpaRating(mpaRating);
    }

    @DeleteMapping("/{mpaRatingId}")
    public void deleteMpaRating(@PathVariable long mpaRatingId) {
        log.debug("Обработан DELETE MpaRating {} запрос.", mpaRatingId);
        mpaRatingService.deleteMpaRating(mpaRatingId);
    }

    @GetMapping
    public Collection<MpaRating> getAllMpaRatings() {
        log.debug("Обработан GET MpaRatings запрос");
        return mpaRatingService.getAllMpaRatings();
    }
}
