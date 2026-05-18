package ru.mephi.vikingdemo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.mephi.vikingdemo.model.Viking;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.IntStream;

@Service
public class VikingService {
    // каждый раз при изменении создаётся новая копия списка
    private final CopyOnWriteArrayList<Viking> vikings = new CopyOnWriteArrayList<>();
    private final VikingFactory vikingFactory;

    @Autowired
    public VikingService(VikingFactory vikingFactory) {
        this.vikingFactory = vikingFactory;
    }

    public List<Viking> findAll() {
        return List.copyOf(vikings);
    }

    public Viking createRandomViking() {
        Viking viking = vikingFactory.createRandomViking();
        vikings.add(viking);
        return viking;
    }

    public Viking addViking(Viking viking) {
        vikings.add(viking);
        return viking;
    }

    public boolean removeViking(String name) {
        return vikings.stream()
                .filter(viking -> viking.name().equalsIgnoreCase(name))
                .findFirst()
                .map(vikings::remove)
                .orElse(false);
    }

    public Optional<Viking> updateViking(String name, Viking updatedViking) {
        return IntStream.range(0, vikings.size())
                .filter(index -> vikings.get(index).name().equalsIgnoreCase(name))
                .findFirst()
                .stream()
                .mapToObj(index -> {
                    vikings.set(index, updatedViking);
                    return updatedViking;
                })
                .findFirst();
    }

    /**
     * Массовая генерация строго по действию пользователя: метод ничего не вызывает сам,
     * а создаёт заданное количество викингов через фабрику и lambda/stream-операции.
     */
    public List<Viking> generateRandomVikings(int count) {
        if (count <= 0) {
            return List.of();
        }

        List<Viking> generated = IntStream.range(0, count)
                .mapToObj(index -> vikingFactory.createRandomViking())
                .toList();

        vikings.addAll(generated);
        return generated;
    }
}
