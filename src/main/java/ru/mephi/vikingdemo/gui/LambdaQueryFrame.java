package ru.mephi.vikingdemo.gui;

import ru.mephi.vikingdemo.model.BeardStyle;
import ru.mephi.vikingdemo.model.HairColor;
import ru.mephi.vikingdemo.model.Viking;
import ru.mephi.vikingdemo.service.LambdaVikingService;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class LambdaQueryFrame extends JFrame {

    private final LambdaVikingService lambdaVikingService;
    private final JTextArea output = new JTextArea();

    public LambdaQueryFrame(LambdaVikingService lambdaVikingService) {
        this.lambdaVikingService = lambdaVikingService;

        setTitle("Lambda Viking Queries");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(new Dimension(900, 520));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        output.setEditable(false);
        output.setLineWrap(true);
        output.setWrapStyleWord(true);

        JPanel buttons = new JPanel(new GridLayout(0, 2, 8, 8));

        addButton(buttons, "Count age > N", this::showCountAgeGreaterThan);
        addButton(buttons, "Count age < N", this::showCountAgeLessThan);
        addButton(buttons, "Count age in range", this::showCountAgeInRange);
        addButton(buttons, "Count age outside range", this::showCountAgeOutsideRange);
        addButton(buttons, "Count beard + hair", this::showCountBeardAndHair);
        addButton(buttons, "Count one axe", () -> print("Викингов с одним топором: " + lambdaVikingService.countWithOneAxe()));
        addButton(buttons, "Count two axes", () -> print("Викингов с двумя топорами: " + lambdaVikingService.countWithTwoAxes()));
        addButton(buttons, "Random height > 180", this::showRandomTallViking);
        addButton(buttons, "Legendary equipment", this::showLegendaryEquipment);
        addButton(buttons, "Red-bearded sorted by age", this::showRedBeardedSorted);
        addButton(buttons, "Max ID", this::showMaxId);
        addButton(buttons, "Even IDs", this::showEvenIds);

        add(new JLabel("Lambda queries for vikings"), BorderLayout.NORTH);
        add(new JScrollPane(output), BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);
    }

    private void addButton(JPanel panel, String text, Runnable action) {
        JButton button = new JButton(text);
        button.addActionListener(event -> action.run());
        panel.add(button);
    }

    private void showCountAgeGreaterThan() {
        Integer age = askInteger("Возраст больше чем:");
        Optional.ofNullable(age)
                .ifPresent(value -> print("Викингов старше " + value + ": " + lambdaVikingService.countAgeGreaterThan(value)));
    }

    private void showCountAgeLessThan() {
        Integer age = askInteger("Возраст меньше чем:");
        Optional.ofNullable(age)
                .ifPresent(value -> print("Викингов младше " + value + ": " + lambdaVikingService.countAgeLessThan(value)));
    }

    private void showCountAgeInRange() {
        int[] range = askRange();
        Optional.ofNullable(range)
                .ifPresent(value -> print("Викингов в диапазоне " + value[0] + "-" + value[1] + ": "
                        + lambdaVikingService.countAgeInRange(value[0], value[1])));
    }

    private void showCountAgeOutsideRange() {
        int[] range = askRange();
        Optional.ofNullable(range)
                .ifPresent(value -> print("Викингов вне диапазона " + value[0] + "-" + value[1] + ": "
                        + lambdaVikingService.countAgeOutsideRange(value[0], value[1])));
    }

    private void showCountBeardAndHair() {
        JComboBox<BeardStyle> beardBox = new JComboBox<>(BeardStyle.values());
        JComboBox<HairColor> hairBox = new JComboBox<>(HairColor.values());

        JPanel panel = new JPanel(new GridLayout(0, 2));
        panel.add(new JLabel("Форма бороды:"));
        panel.add(beardBox);
        panel.add(new JLabel("Цвет волос:"));
        panel.add(hairBox);

        int result = JOptionPane.showConfirmDialog(this, panel, "Beard + Hair", JOptionPane.OK_CANCEL_OPTION);
        Optional.of(result)
                .filter(value -> value == JOptionPane.OK_OPTION)
                .ifPresent(value -> {
                    BeardStyle beard = (BeardStyle) beardBox.getSelectedItem();
                    HairColor hair = (HairColor) hairBox.getSelectedItem();
                    print("Викингов с бородой " + beard + " и волосами " + hair + ": "
                            + lambdaVikingService.countByBeardStyleAndHairColor(beard, hair));
                });
    }

    private void showRandomTallViking() {
        print(lambdaVikingService.findRandomVikingTallerThan180()
                .map(this::formatViking)
                .orElse("Не найден викинг ростом выше 180 см"));
    }

    private void showLegendaryEquipment() {
        printList("Викинги с легендарным снаряжением", lambdaVikingService.findAllWithLegendaryEquipment());
    }

    private void showRedBeardedSorted() {
        printList("Рыжебородые викинги, сортировка по возрасту", lambdaVikingService.findRedBeardedSortedByAge());
    }

    private void showMaxId() {
        Integer[] ids = askIds();
        Optional.ofNullable(ids)
                .flatMap(lambdaVikingService::findMaxId)
                .ifPresentOrElse(
                        id -> print("Последняя запись / max ID: " + id),
                        () -> print("ID не найдены")
                );
    }

    private void showEvenIds() {
        Integer[] ids = askIds();
        Optional.ofNullable(ids)
                .map(lambdaVikingService::findEvenIds)
                .ifPresent(idsList -> print("Четные ID: " + idsList));
    }

    private Integer askInteger(String message) {
        return Optional.ofNullable(JOptionPane.showInputDialog(this, message))
                .map(String::trim)
                .filter(text -> !text.isBlank())
                .map(Integer::parseInt)
                .orElse(null);
    }

    private int[] askRange() {
        Integer min = askInteger("Минимальный возраст:");
        Integer max = askInteger("Максимальный возраст:");
        return Optional.ofNullable(min)
                .flatMap(minValue -> Optional.ofNullable(max)
                        .map(maxValue -> new int[]{Math.min(minValue, maxValue), Math.max(minValue, maxValue)}))
                .orElse(null);
    }

    private Integer[] askIds() {
        String input = JOptionPane.showInputDialog(this, "Введите ID через запятую:", "1, 2, 3, 4, 10", JOptionPane.QUESTION_MESSAGE);
        return Optional.ofNullable(input)
                .map(text -> text.split(","))
                .map(parts -> Arrays.stream(parts)
                        .map(String::trim)
                        .filter(value -> !value.isBlank())
                        .map(Integer::parseInt)
                        .toArray(Integer[]::new))
                .orElse(null);
    }

    private void printList(String title, List<Viking> vikings) {
        String text = vikings.stream()
                .map(this::formatViking)
                .collect(Collectors.joining("\n"));
        print(title + ":\n" + (text.isBlank() ? "Ничего не найдено" : text));
    }

    private String formatViking(Viking viking) {
        return viking.name()
                + ", age=" + viking.age()
                + ", height=" + viking.heightCm()
                + ", hair=" + viking.hairColor()
                + ", beard=" + viking.beardStyle()
                + ", equipment=" + viking.equipment();
    }

    private void print(String text) {
        output.setText(text);
    }
}
