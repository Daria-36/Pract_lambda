package ru.mephi.vikingdemo.service;

import org.springframework.stereotype.Service;
import ru.mephi.vikingdemo.model.BeardStyle;
import ru.mephi.vikingdemo.model.EquipmentItem;
import ru.mephi.vikingdemo.model.HairColor;
import ru.mephi.vikingdemo.model.Viking;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;

/**
 * Сервис для заданий по лямбда-функциям и Stream API.
 * Все вычисления выполняются через Predicate, Function/Comparator и stream/lambda-операции.
 */
@Service
public class LambdaVikingService {

    private final VikingService vikingService;

    public LambdaVikingService(VikingService vikingService) {
        this.vikingService = vikingService;
    }

    public long countAgeGreaterThan(int age) {
        return countByCondition(viking -> viking.age() > age);
    }

    public long countAgeLessThan(int age) {
        return countByCondition(viking -> viking.age() < age);
    }

    public long countAgeInRange(int minAgeInclusive, int maxAgeInclusive) {
        return countByCondition(viking -> viking.age() >= minAgeInclusive && viking.age() <= maxAgeInclusive);
    }

    public long countAgeOutsideRange(int minAgeInclusive, int maxAgeInclusive) {
        return countByCondition(viking -> viking.age() < minAgeInclusive || viking.age() > maxAgeInclusive);
    }

    public long countByBeardStyleAndHairColor(BeardStyle beardStyle, HairColor hairColor) {
        return countByCondition(viking -> viking.beardStyle() == beardStyle && viking.hairColor() == hairColor);
    }

    public long countWithOneAxe() {
        return countWithAxeAmount(1);
    }

    public long countWithTwoAxes() {
        return countWithAxeAmount(2);
    }

    public Optional<Viking> findRandomVikingTallerThan180() {
        List<Viking> tallVikings = vikingService.findAll().stream()
                .filter(viking -> viking.heightCm() > 180)
                .toList();

        return Optional.of(tallVikings)
                .filter(list -> !list.isEmpty())
                .map(list -> list.get(ThreadLocalRandom.current().nextInt(list.size())));
    }

    public List<Viking> findAllWithLegendaryEquipment() {
        return vikingService.findAll().stream()
                .filter(viking -> viking.equipment().stream()
                        .anyMatch(item -> "Legendary".equalsIgnoreCase(item.quality())))
                .toList();
    }

    /**
     * В модели нет отдельного цвета бороды, поэтому "рыжебородые" трактуются как:
     * рыжие волосы + борода не CLEAN_SHAVEN.
     */
    public List<Viking> findRedBeardedSortedByAge() {
        return vikingService.findAll().stream()
                .filter(viking -> viking.hairColor() == HairColor.Red)
                .filter(viking -> viking.beardStyle() != BeardStyle.CLEAN_SHAVEN)
                .sorted(Comparator.comparingInt(Viking::age))
                .toList();
    }

    public Optional<Integer> findMaxId(Integer[] ids) {
        return Arrays.stream(ids)
                .max(Integer::compareTo);
    }

    public List<Integer> findEvenIds(Integer[] ids) {
        return Arrays.stream(ids)
                .filter(id -> id % 2 == 0)
                .toList();
    }

    private long countByCondition(Predicate<Viking> condition) {
        return vikingService.findAll().stream()
                .filter(condition)
                .count();
    }

    private long countWithAxeAmount(long axeAmount) {
        return countByCondition(viking -> viking.equipment().stream()
                .map(EquipmentItem::name)
                .filter(name -> "Axe".equalsIgnoreCase(name))
                .count() == axeAmount);
    }
}
