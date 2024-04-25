package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.MpaRatingStorage;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MpaRatingService {

    private final MpaRatingStorage mpaRatingStorage;

    public MpaRating addMpaRating(MpaRating mpaRating) {
        return mpaRatingStorage.create(mpaRating);
    }

    public MpaRating getMpaRating(Long id) {
        return mpaRatingStorage.get(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("MPA рейтинг с id=%d не найден", id)));
    }

    public MpaRating updateMpaRating(MpaRating mpaRating) {
        getMpaRating((long) mpaRating.getId());
        return mpaRatingStorage.update(mpaRating);
    }

    public void deleteMpaRating(long mpaRatingId) {
        getMpaRating(mpaRatingId);
        mpaRatingStorage.delete(mpaRatingId);
    }

    public List<MpaRating> getAllMpaRatings() {
        return (List<MpaRating>) mpaRatingStorage.getAll();
    }

}
