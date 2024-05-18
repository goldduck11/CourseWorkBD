package MainClasses;

import Connect.MyConnection;
import lombok.SneakyThrows;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class AdminWindow extends JFrame {

    private DefaultListModel<String> productsModel;
    private DefaultListModel<String> ordersModel;
    private DefaultListModel<String> suppliersModel;
    private DefaultListModel<String> warehousesModel;

    public AdminWindow() {
        super("Панель администратора");
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
            @SneakyThrows
            @Override
            public void actionPerformed(ActionEvent e) {
                if (titleLabel.getText().equals("Товары")) {
                    productAdd();
                }
                if (titleLabel.getText().equals("Заказы")) {
                    orderAdd();
                }
                if (titleLabel.getText().equals("Поставщики")) {
                    requsitionAdd();
                }
                if (titleLabel.getText().equals("Склады")) {
                    supplierAdd();
                }
            }
        });
        JButton deleteButton = new JButton("Удалить");
        deleteButton.addActionListener(new ActionListener() {
            @SneakyThrows
            @Override
            public void actionPerformed(ActionEvent e) {
                if (titleLabel.getText().equals("Товары")) {
                    
                }
                if (titleLabel.getText().equals("Заказы")) {
                }
                if (titleLabel.getText().equals("Поставщики")) {
                }
                if (titleLabel.getText().equals("Склады")) {
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

    private void supplierAdd() throws SQLException {
        List<JTextField> textFields = new ArrayList<>();
        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5)); // сетка для n строк и 2 столбца
        panel.add(new Label("Расположение: "));
        textFields.add(new JTextField(10));
        panel.add(textFields.getLast());

        panel.add(new Label("Вместимость: "));
        textFields.add(new JTextField(10));
        panel.add(textFields.getLast());

        panel.add(new Label("Занятость: "));
        textFields.add(new JTextField(10));
        panel.add(textFields.getLast());

        int result = JOptionPane.showConfirmDialog(AdminWindow.this,
                panel, "Введите данные", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            // Если нажата кнопка OK, получаем значения из всех полей ввода
            String location = textFields.get(0).getText();
            String capacity = textFields.get(1).getText();
            String occupancy = textFields.get(2).getText();
            if (location.isEmpty() || capacity.isEmpty() || occupancy.isEmpty()) {
                JOptionPane.showMessageDialog(panel,
                        "Все поля должны быть заполнены!");
            }


            String insertQuery = "INSERT INTO public.storage (name, price , quantity) VALUES (?, ?, ?)";
            PreparedStatement statement = MyConnection.getConnection().prepareStatement(insertQuery);
            statement.setString(1, location);      // Устанавливаем значение для параметра login
            statement.setLong(2, Long.parseLong(capacity));   // Устанавливаем значение для параметра password
            statement.setLong(3, Long.parseLong(occupancy));       // Устанавливаем значение для параметра role

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0)
                JOptionPane.showMessageDialog(AdminWindow.this, "Данные были успешно добавлены в базу!");
            else
                JOptionPane.showMessageDialog(AdminWindow.this, "Данные не были добавлены в базу!");

        } else {
            System.out.println("Диалог отменен");
        }
    }

    private void requsitionAdd() throws SQLException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        List<JTextField> textFields = new ArrayList<>();
        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5)); // сетка для n строк и 2 столбца
        panel.add(new Label("Статус: "));
        textFields.add(new JTextField(10));
        panel.add(textFields.getLast());

        panel.add(new Label("Дата: "));
        textFields.add(new JTextField(10));
        panel.add(textFields.getLast());

        panel.add(new Label("Имя пользователя: "));
        textFields.add(new JTextField(10));
        panel.add(textFields.getLast());

        int result = JOptionPane.showConfirmDialog(AdminWindow.this,
                panel, "Введите данные", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            // Если нажата кнопка OK, получаем значения из всех полей ввода
            String status = textFields.get(0).getText();
            Date date = null;
            try {
                date = (Date) formatter.parse(textFields.get(1).getText());
            } catch (ParseException ex) {
                JOptionPane.showMessageDialog(AdminWindow.this,
                        "Неверный формат даты!");
            }
            String userName = textFields.get(2).getText();

            if (status.isEmpty() || userName.isEmpty()) {
                JOptionPane.showMessageDialog(panel,
                        "Все поля должны быть заполнены!");
            }


            ResultSet userId = MyConnection.getStatement().executeQuery("select id from public.user " +
                    "where name = '" + userName + "' LIMIT 1");
            String insertQuery = "INSERT INTO public.requisition (location, capacity, occupancy) VALUES (?, ?, ?)";
            PreparedStatement statement = MyConnection.getConnection().prepareStatement(insertQuery);
            statement.setString(1, status);      // Устанавливаем значение для параметра login
            statement.setDate(2, date);   // Устанавливаем значение для параметра password
            statement.setLong(3, userId.next() ? userId.getLong("id") : Long.parseLong("gwrg"));       // Устанавливаем значение для параметра role

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0)
                JOptionPane.showMessageDialog(AdminWindow.this, "Данные были успешно добавлены в базу!");
            else
                JOptionPane.showMessageDialog(AdminWindow.this, "Данные не были добавлены в базу!");

        } else {
            System.out.println("Диалог отменен");
        }
    }

    private void orderAdd() throws SQLException {
        List<JTextField> textFields = new ArrayList<>();
        JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5)); // сетка для n строк и 2 столбца
        panel.add(new Label("Название: "));
        textFields.add(new JTextField(10));
        panel.add(textFields.getLast());

        panel.add(new Label("Адрес: "));
        textFields.add(new JTextField(10));
        panel.add(textFields.getLast());

        panel.add(new Label("Контактная информация: "));
        textFields.add(new JTextField(10));
        panel.add(textFields.getLast());

        panel.add(new Label("Название товара: "));
        textFields.add(new JTextField(10));
        panel.add(textFields.getLast());

        int result = JOptionPane.showConfirmDialog(AdminWindow.this,
                panel, "Введите данные", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            // Если нажата кнопка OK, получаем значения из всех полей ввода
            String name = textFields.get(0).getText();
            String address = textFields.get(1).getText();
            String contactInfo = textFields.get(2).getText();
            String productName = textFields.get(3).getText();
            if (name.isEmpty() || address.isEmpty() || contactInfo.isEmpty() || productName.isEmpty()) {
                JOptionPane.showMessageDialog(panel,
                        "Все поля должны быть заполнены!");
            }


            ResultSet productId = MyConnection.getStatement().executeQuery("select id from public.product " +
                    "where name = '" + productName + "' LIMIT 1");
            String insertQuery = "INSERT INTO public.supplier (name, location, contact_information, " +
                    "product_id) VALUES (?, ?, ?, ?)";
            PreparedStatement statement = MyConnection.getConnection().prepareStatement(insertQuery);
            statement.setString(1, name);      // Устанавливаем значение для параметра login
            statement.setString(2, address);   // Устанавливаем значение для параметра password
            statement.setString(3, contactInfo);       // Устанавливаем значение для параметра role
            statement.setLong(4, productId.next() ? productId.getLong("id") : Long.parseLong("gwrg"));

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0)
                JOptionPane.showMessageDialog(AdminWindow.this, "Данные были успешно добавлены в базу!");
            else
                JOptionPane.showMessageDialog(AdminWindow.this, "Данные не были добавлены в базу!");

        } else {
            System.out.println("Диалог отменен");
        }
    }

    private void productAdd() throws SQLException {
        List<JTextField> textFields = new ArrayList<>();
        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5)); // сетка для n строк и 2 столбца
        panel.add(new Label("Название: "));
        textFields.add(new JTextField(10));
        panel.add(textFields.getLast());

        panel.add(new Label("Цена: "));
        textFields.add(new JTextField(10));
        panel.add(textFields.getLast());

        panel.add(new Label("Количество: "));
        textFields.add(new JTextField(10));
        panel.add(textFields.getLast());

        int result = JOptionPane.showConfirmDialog(AdminWindow.this,
                panel, "Введите данные", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            // Если нажата кнопка OK, получаем значения из всех полей ввода
            String name = textFields.get(0).getText();
            String price = textFields.get(1).getText();
            String count = textFields.get(2).getText();
            if (name.isEmpty() || price.isEmpty() || count.isEmpty()) {
                JOptionPane.showMessageDialog(panel,
                        "Все поля должны быть заполнены!");
            }


            String insertQuery = "INSERT INTO public.product (name, price , quantity) VALUES (?, ?, ?)";
            PreparedStatement statement = MyConnection.getConnection().prepareStatement(insertQuery);
            statement.setString(1, name);      // Устанавливаем значение для параметра login
            statement.setDouble(2, Double.parseDouble(price));   // Устанавливаем значение для параметра password
            statement.setLong(3, Long.parseLong(count));       // Устанавливаем значение для параметра role

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0)
                JOptionPane.showMessageDialog(AdminWindow.this, "Данные были успешно добавлены в базу!");
            else
                JOptionPane.showMessageDialog(AdminWindow.this, "Данные не были добавлены в базу!");

        } else {
            System.out.println("Диалог отменен");
        }
    }
}
