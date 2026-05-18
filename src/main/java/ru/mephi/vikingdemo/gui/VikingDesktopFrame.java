package ru.mephi.vikingdemo.gui;

import ru.mephi.vikingdemo.model.Viking;
import ru.mephi.vikingdemo.service.LambdaVikingService;
import ru.mephi.vikingdemo.service.VikingService;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.util.List;

public class VikingDesktopFrame extends JFrame {

    private final VikingService vikingService;
    private final LambdaVikingService lambdaVikingService;
    private final VikingTableModel tableModel = new VikingTableModel();

    public VikingDesktopFrame(VikingService vikingService, LambdaVikingService lambdaVikingService) {
        this.vikingService = vikingService;
        this.lambdaVikingService = lambdaVikingService;

        setTitle("Viking Demo");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(new Dimension(1000, 420));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JLabel header = new JLabel("Viking Demo", SwingConstants.CENTER);
        header.setFont(header.getFont().deriveFont(Font.BOLD, 18f));
        add(header, BorderLayout.NORTH);

        JTable vikingTable = new JTable(tableModel);
        vikingTable.setRowHeight(28);
        add(new JScrollPane(vikingTable), BorderLayout.CENTER);

        JButton createButton = new JButton("Create random viking");
        createButton.addActionListener(event -> onCreateViking());

        JButton generateButton = new JButton("Generate vikings");
        generateButton.addActionListener(event -> onGenerateVikings());

        JButton lambdaButton = new JButton("Lambda queries");
        lambdaButton.addActionListener(event -> new LambdaQueryFrame(lambdaVikingService).setVisible(true));

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(createButton);
        bottomPanel.add(generateButton);
        bottomPanel.add(lambdaButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void onCreateViking() {
        Viking viking = vikingService.createRandomViking();
        tableModel.addViking(viking);
    }

    private void onGenerateVikings() {
        String input = JOptionPane.showInputDialog(this, "Сколько викингов сгенерировать?", "10");
        if (input == null || input.isBlank()) {
            return;
        }

        int count = Integer.parseInt(input.trim());
        List<Viking> generated = vikingService.generateRandomVikings(count);
        generated.forEach(tableModel::addViking);
    }

    public void addNewViking(Viking viking) {
        tableModel.addViking(viking);
    }

    public void addNewVikings(List<Viking> vikings) {
        vikings.forEach(tableModel::addViking);
    }

    public void removeViking(String name) {
        tableModel.removeViking(name);
    }

    public void updateViking(String name, Viking viking) {
        tableModel.updateViking(name, viking);
    }
}
