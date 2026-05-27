package ru.mephi.vikingdemo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.mephi.vikingdemo.model.Viking;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;


public class VikingService {

    // Викинги теперь хранятся именно в массиве, а не в списке
    private Viking[] vikings = new Viking[0];

    private final VikingFactory vikingFactory;
    
    public VikingService(VikingFactory vikingFactory) {
        this.vikingFactory = vikingFactory;
    }

    // Для старого кода GUI/API оставляем List, чтобы не сломать контроллеры и таблицу
    public synchronized List<Viking> findAll() {
        return Arrays.stream(vikings).toList();
    }

    // Новый метод: возвращает копию массива викингов
    public synchronized Viking[] findAllAsArray() {
        return Arrays.copyOf(vikings, vikings.length);
    }

    public synchronized Viking createRandomViking() {
        Viking viking = vikingFactory.createRandomViking();
        addViking(viking);
        return viking;
    }

    public synchronized Viking addViking(Viking viking) {
        Viking[] newArray = Arrays.copyOf(vikings, vikings.length + 1);
        newArray[newArray.length - 1] = viking;
        vikings = newArray;
        return viking;
    }

    public synchronized boolean removeViking(String name) {
        int oldLength = vikings.length;

        vikings = Arrays.stream(vikings)
                .filter(viking -> !viking.name().equalsIgnoreCase(name))
                .toArray(Viking[]::new);

        return vikings.length < oldLength;
    }

    public synchronized Optional<Viking> updateViking(String name, Viking updatedViking) {
        return IntStream.range(0, vikings.length)
                .filter(index -> vikings[index].name().equalsIgnoreCase(name))
                .findFirst()
                .stream()
                .mapToObj(index -> {
                    vikings[index] = updatedViking;
                    return updatedViking;
                })
                .findFirst();
    }

    public synchronized List<Viking> generateRandomVikings(int count) {
        if (count <= 0) {
            return List.of();
        }

        Viking[] generated = IntStream.range(0, count)
                .mapToObj(index -> vikingFactory.createRandomViking())
                .toArray(Viking[]::new);

        Viking[] newArray = Arrays.copyOf(vikings, vikings.length + generated.length);
        System.arraycopy(generated, 0, newArray, vikings.length, generated.length);

        vikings = newArray;

        return Arrays.stream(generated).toList();
    }
}
