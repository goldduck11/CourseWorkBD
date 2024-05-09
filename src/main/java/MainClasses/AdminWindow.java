package MainClasses;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AdminWindow extends JFrame {

    private DefaultListModel<String> productsModel;
    private DefaultListModel<String> ordersModel;
    private DefaultListModel<String> suppliersModel;
    private DefaultListModel<String> warehousesModel;

    public AdminWindow() {
        super("Панель администратора");hui
        initComponents();

        // Установка параметров окна
        setSize(800, 400); // Увеличим ширину для добавления списка "Склады"
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Центрировать окно по центру экрана
        setVisible(true);
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new GridLayout(1, 4, 10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Создание моделей для списков
        productsModel = new DefaultListModel<>();
        ordersModel = new DefaultListModel<>();
        suppliersModel = new DefaultListModel<>();
        warehousesModel = new DefaultListModel<>();

        // Добавление элементов по умолчанию в списки (для демонстрации)
        productsModel.addElement("Товар 1");
        productsModel.addElement("Товар 2");
        ordersModel.addElement("Заказ 1");
        ordersModel.addElement("Заказ 2");
        suppliersModel.addElement("Поставщик 1");
        suppliersModel.addElement("Поставщик 2");
        warehousesModel.addElement("Склад 1");
        warehousesModel.addElement("Склад 2");

        // Создание списков
        JList<String> productsList = new JList<>(productsModel);
        JList<String> ordersList = new JList<>(ordersModel);
        JList<String> suppliersList = new JList<>(suppliersModel);
        JList<String> warehousesList = new JList<>(warehousesModel);

        // Добавление списков на панель
        mainPanel.add(createListPanel("Товары", productsList));
        mainPanel.add(createListPanel("Заказы", ordersList));
        mainPanel.add(createListPanel("Поставщики", suppliersList));
        mainPanel.add(createListPanel("Склады", warehousesList));

        // Добавление основной панели на окно
        add(mainPanel);
    }

    private JPanel createListPanel(String title, JList<String> list) {
        JPanel panel = new JPanel(new BorderLayout());

        // Название списка
        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        panel.add(titleLabel, BorderLayout.NORTH);

        // Панель с кнопками "Добавить" и "Удалить"
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 5, 5));
        JButton addButton = new JButton("Добавить");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String newItem = JOptionPane.showInputDialog(AdminWindow.this, "Введите новый элемент:");
                if (newItem != null && !newItem.isEmpty()) {
                    DefaultListModel<String> model = (DefaultListModel<String>) list.getModel();
                    model.addElement(newItem);
                }
            }
        });
        JButton deleteButton = new JButton("Удалить");
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedIndex = list.getSelectedIndex();
                if (selectedIndex != -1) {
                    DefaultListModel<String> model = (DefaultListModel<String>) list.getModel();
                    model.remove(selectedIndex);
                } else {
                    JOptionPane.showMessageDialog(AdminWindow.this, "Выберите элемент для удаления.");
                }
            }
        });
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Добавление списка на панель
        JScrollPane scrollPane = new JScrollPane(list);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    public static void main(String[] args) {
        // Создание и отображение окна панели администратора
        SwingUtilities.invokeLater(AdminWindow::new);
    }
}
