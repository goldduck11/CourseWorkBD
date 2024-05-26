package MainClasses;

import Connect.HibernateUtil;
import Connect.MyConnection;
import Graph.PieGraph;
import lombok.SneakyThrows;
import Enum.RequisitionStatus;
import org.example.Entity.Product;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import org.hibernate.query.Query;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileWriter;
import java.io.IOException;
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
    private DefaultListModel<String> storageModel;

    public AdminWindow() {
        super("Панель администратора");
        initComponents();

        // Установка параметров окна
        setSize(800, 400); // Увеличим ширину для добавления списка "Склады"
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Центрировать окно по центру экрана
        setVisible(true);
    }

    @SneakyThrows
    private void initComponents() {
        JPanel mainPanel = new JPanel(new GridLayout(1, 4, 10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JMenu menu = new JMenu("Опции");
        JMenuItem recordButton = new JMenuItem("Получить отчёт");
        JMenuItem graphButton = new JMenuItem("Отобразить график");
        menu.add(recordButton);
        menu.add(graphButton);
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(menu);

        // Создание моделей для списков
        productsModel = new DefaultListModel<>();
        ordersModel = new DefaultListModel<>();
        suppliersModel = new DefaultListModel<>();
        storageModel = new DefaultListModel<>();

        // Добавление элементов по умолчанию в списки (для демонстрации)
        displayProductData();
        displayOrderData();
        displaySuppliersData();
        displayStorageData();

        // Создание списков
        JList<String> productsList = new JList<>(productsModel);
        JList<String> ordersList = new JList<>(ordersModel);
        JList<String> suppliersList = new JList<>(suppliersModel);
        JList<String> storageList = new JList<>(storageModel);

        // Добавление списков на панель
        setJMenuBar(menuBar);
        mainPanel.add(createListPanel("Товары", productsList));
        mainPanel.add(createListPanel("Заказы", ordersList));
        mainPanel.add(createListPanel("Поставщики", suppliersList));
        mainPanel.add(createListPanel("Склады", storageList));

        // Добавление основной панели на окно
        add(mainPanel);

        graphButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Session session = HibernateUtil.getSession();
                List<Product> productsData = session.createQuery("from Product", Product.class).getResultList();
                SwingUtilities.invokeLater(() -> {
                    PieGraph pieGraph = new PieGraph(productsData);
                    pieGraph.setLocationRelativeTo(null);
                    pieGraph.pack();
                    pieGraph.setVisible(true);
                });
            }
        });

        recordButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Integer index = productsList.getSelectedIndex();
                if (index == -1) {
                    JOptionPane.showMessageDialog(AdminWindow.this, "Выберите один из товаров!");
                    return;
                }
                String value = productsList.getSelectedValue();
                Product product = HibernateUtil.getSession().createQuery("from Product where name = :name", Product.class)
                        .setParameter("name", value)
                        .getSingleResult();
                exportDataToFile(product);
            }
        });
    }

    private void displayStorageData() throws SQLException {
        ResultSet rs = MyConnection.getStatement().executeQuery("select * from public.storage");
        while (rs.next()) {
            storageModel.addElement(rs.getString("location"));
        }
    }

    private void displaySuppliersData() throws SQLException {
        ResultSet rs = MyConnection.getStatement().executeQuery("select * from public.supplier");
        while (rs.next()) {
            suppliersModel.addElement(rs.getString("name"));
        }
    }

    private void displayOrderData() throws SQLException {
        ResultSet rs = MyConnection.getStatement().executeQuery("select * from public.requisition");
        while (rs.next()) {
            ordersModel.addElement(rs.getString("id"));
        }
    }

    private void displayProductData() throws SQLException {
        ResultSet rs = MyConnection.getStatement().executeQuery("select * from public.product");
        while (rs.next()) {
            productsModel.addElement(rs.getString("name"));
        }
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
                    AdminWindow.this.initComponents();
                    return;
                }
                if (titleLabel.getText().equals("Заказы")) {
                    orderAdd();
                    AdminWindow.this.initComponents();
                    return;
                }
                if (titleLabel.getText().equals("Поставщики")) {
                    requsitionAdd();
                    AdminWindow.this.initComponents();
                    return;
                }
                if (titleLabel.getText().equals("Склады")) {
                    supplierAdd();
                    AdminWindow.this.initComponents();
                }
            }
        });
        JButton deleteButton = new JButton("Удалить");
        deleteButton.addActionListener(new ActionListener() {
            @SneakyThrows
            @Override
            public void actionPerformed(ActionEvent e) {
                if (titleLabel.getText().equals("Товары")) {
                    delateDataFromBD(list, "DELETE FROM public.product WHERE name = ?");
                }
                if (titleLabel.getText().equals("Заказы")) {
                    delateDataFromBD(list, "DELETE FROM public.requisition WHERE name = ?");
                }
                if (titleLabel.getText().equals("Поставщики")) {
                    delateDataFromBD(list, "DELETE FROM public.supplier WHERE name = ?");
                }
                if (titleLabel.getText().equals("Склады")) {
                    delateDataFromBD(list, "DELETE FROM public.storage WHERE name = ?");
                }
            }
        });

        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        list.addMouseListener(new MouseAdapter() {
            @SneakyThrows
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int index = list.locationToIndex(e.getPoint());
                    if (index >= 0) {
                        // Получаем выбранный элемент
                        String item = list.getModel().getElementAt(index);
                        if (titleLabel.getText().equals("Товары")) {
                            doubleClickFromProduct(item);
                        } else if (titleLabel.getText().equals("Заказы")) {
                            doubleClickFromOrder(item);
                        } else if (titleLabel.getText().equals("Поставщики")) {
                            doubleClickFromSupplier(item);
                        } else if (titleLabel.getText().equals("Склады")) {
                            doubleClickFromStorage(item);
                        }
                    }
                }
            }
        });
        // Добавление списка на панель
        JScrollPane scrollPane = new JScrollPane(list);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void doubleClickFromStorage(String item) throws SQLException {
        ResultSet rs = MyConnection.getStatement().executeQuery("select * from public.storage where location = '" + item + "'");
        if (!rs.next()) {
            JOptionPane.showMessageDialog(AdminWindow.this, "Данные товара не найдены!");
        }
        Long capacity = rs.getLong("capacity");
        Long occupancy = rs.getLong("occupancy");

        JPanel panel = new JPanel(new GridLayout(5, 2, 5, 5)); // сетка для n строк и 2 столбца

        panel.add(new Label("Местонахождение склада: "));
        panel.add(new Label(item));

        panel.add(new Label("Вместимость: "));
        panel.add(new Label(String.valueOf(capacity)));

        panel.add(new Label("Заполненность: "));
        panel.add(new Label(String.valueOf(occupancy)));

        JOptionPane.showMessageDialog(AdminWindow.this, panel);
    }

    private void doubleClickFromSupplier(String item) throws SQLException {
        ResultSet rs = MyConnection.getStatement().executeQuery("select location, contact_information from public.requisition where name = '" + item + "'");
        if (!rs.next()) {
            JOptionPane.showMessageDialog(AdminWindow.this, "Данные поставщика не найдены");
        }
        String location = rs.getString("location");
        String contact_info = rs.getString("contact_information");
        ArrayList<Long> productsIds = new ArrayList<>();
        rs = MyConnection.getStatement().executeQuery("select product_id from public.product where name = '" + item + "'");
        while (rs.next()) {
            productsIds.add(rs.getLong(1));
        }

        JPanel panel = new JPanel(new GridLayout(4 + productsIds.size() * 4, 2, 5, 5)); // сетка для n строк и 2 столбца
        panel.add(new Label("Имя поставщика: "));
        panel.add(new Label(item));

        panel.add(new Label("Местоположение поставщика: "));
        panel.add(new Label(location));

        panel.add(new Label("Контактная инфрмация: "));
        panel.add(new Label(contact_info));

        panel.add(new Label());
        panel.add(new Label());

        panel.add(new Label("Информация о товарах: "));
        panel.add(new Label());

        for (int i = 0; i < productsIds.size(); i++) {
            rs = MyConnection.getStatement().executeQuery("select * from public.product where id = '" + productsIds.get(i) + "'");

            if (!rs.next()) {
                JOptionPane.showMessageDialog(AdminWindow.this, "Данные товара не найдены!");
            }
            String name = rs.getString("name");
            Double price = rs.getDouble("price");
            Long quantity = rs.getLong("quantity");

            panel.add(new Label("Название товара: "));
            panel.add(new Label(name));

            panel.add(new Label("Количество товара: "));
            panel.add(new Label(quantity.toString()));

            panel.add(new Label("Цена товара(за штуку): "));
            panel.add(new Label(String.valueOf(price)));

            panel.add(new Label());
            panel.add(new Label());
        }

        JOptionPane.showMessageDialog(AdminWindow.this, panel);
    }

    private void doubleClickFromProduct(String item) throws SQLException {
        ResultSet rs = MyConnection.getStatement().executeQuery("select * from public.product where name = '" + item + "'");
        if (!rs.next()) {
            JOptionPane.showMessageDialog(AdminWindow.this, "Данные товара не найдены!");
        }
        Double price = rs.getDouble("price");
        Long quantity = rs.getLong("quantity");

        JPanel panel = new JPanel(new GridLayout(5, 2, 5, 5)); // сетка для n строк и 2 столбца
        panel.add(new Label("Название товара: "));
        panel.add(new Label(item));

        panel.add(new Label("Количество товара: "));
        panel.add(new Label(quantity.toString()));

        panel.add(new Label("Цена товара(за штуку): "));
        panel.add(new Label(String.valueOf(price)));

        JOptionPane.showMessageDialog(AdminWindow.this, panel);
    }

    private void doubleClickFromOrder(String item) throws SQLException {
        ResultSet rs = MyConnection.getStatement().executeQuery("select * from public.requisition_product where requisition_id = '" + item + "'");
        if (!rs.next()) {
            JOptionPane.showMessageDialog(AdminWindow.this, "Данные заказа не найдены!");
        }
        Long requisitionId = rs.getLong("requisition_id");
        Long product_id = rs.getLong("product_id");
        Long quantity = rs.getLong("quantity");
        Double price = rs.getDouble("price");
        rs = MyConnection.getStatement().executeQuery("select user_id, status from public.requisition where id = '" + item + "'");
        if (!rs.next()) {
            JOptionPane.showMessageDialog(AdminWindow.this, "Данные заказа не найдены");
        }
        String status = rs.getString("status");
        Long user_id = rs.getLong("user_id");

        if (user_id == null) {
            JOptionPane.showMessageDialog(AdminWindow.this, "Данные заказа не найдены!");
        }

        rs = MyConnection.getStatement().executeQuery("select login from public.user where id = '" + user_id + "'");
        String login = rs.next() ? rs.getString("login") : null;

        if (user_id == null) {
            JOptionPane.showMessageDialog(AdminWindow.this, "Данные пользователя не найдены!");
        }

        rs = MyConnection.getStatement().executeQuery("select name from public.product where id = '" + product_id + "'");
        String productName = rs.next() ? rs.getString("name") : null;
        if (productName == null) {
            JOptionPane.showMessageDialog(AdminWindow.this, "Данные товара не найдены!");
        }

        JPanel panel = new JPanel(new GridLayout(5, 2, 5, 5)); // сетка для n строк и 2 столбца
        panel.add(new Label("Пользователь: "));
        panel.add(new Label(login));

        panel.add(new Label("Номер продукта: "));
        panel.add(new Label(product_id.toString()));

        panel.add(new Label("Название товара: "));
        panel.add(new Label(productName));

        panel.add(new Label("Количество товара: "));
        panel.add(new Label(quantity.toString()));

        panel.add(new Label("Общая сумма заказа: "));
        panel.add(new Label(price.toString()));

        if (status.equalsIgnoreCase("НОВАЯ")) {

            String[] options = {"Отпустить товар", "Не отпускать"};

            // Показ диалогового окна с пользовательской панелью и кастомными кнопками
            int result = JOptionPane.showOptionDialog(
                    AdminWindow.this,
                    panel,
                    "Подробности товара",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    options,
                    options[0]
            );

            // Обработка нажатия кнопок
            if (result == JOptionPane.OK_OPTION) {
                int row = MyConnection.getStatement()
                        .executeUpdate("UPDATE public.requisition SET status = '"
                                + RequisitionStatus.COMPLETED + "' WHERE id = '" + item + "'");
                if (row > 0) {
                    JOptionPane.showMessageDialog(panel, "Отпуск отвара был произведён успешно");
                }
            } else if (result == 1) {
                int row = MyConnection.getStatement()
                        .executeUpdate("UPDATE public.requisition SET status = '"
                                + RequisitionStatus.CANCELED + "' WHERE id = '" + item + "'");
                HibernateUtil hibernateUtil = new HibernateUtil();
                hibernateUtil.cancelRequisition(requisitionId);
                if (row > 0) {
                    JOptionPane.showMessageDialog(panel, "Отпуск товара был отменён");
                }
            }
        } else {
            JOptionPane.showMessageDialog(AdminWindow.this, panel);
        }

    }

    private static void delateDataFromBD(JList<String> list, String deleteSQL) throws SQLException {
        String selectedValue = list.getSelectedValue();
        PreparedStatement preparedStatement = MyConnection.getConnection().prepareStatement(deleteSQL);

        // Set the value for the department parameter
        preparedStatement.setString(1, selectedValue);

        // Execute the delete statement
        int rowsAffected = preparedStatement.executeUpdate();

        System.out.println("Deleted " + rowsAffected + " rows from the employees table.");
    }

    private static void exportDataToFile(Product product) {
        // Код для экспорта данных из JTable в файл с названием tableName
        try (FileWriter writer = new FileWriter(product.getName() + product.getId() + ".csv")) {
            writer.write(product.getName() + "\n");
            writer.write(product.getQuantity() + "\n");
            writer.write(product.getPrice() + "\n");
            JOptionPane.showMessageDialog(null,
                    "Данные успешно экспортированы в файл " + product.getName() + product.getId() + ".csv");
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Ошибка при экспорте данных: " + ex.getMessage()
                    , "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
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
