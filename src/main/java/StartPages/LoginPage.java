package StartPages;

import Connect.HibernateUtil;
import MainClasses.AdminWindow;
import MainClasses.UserWindow;
import org.example.Entity.User;
import org.hibernate.Session;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class LoginPage extends JFrame {

    private JTextField loginField;
    private JPasswordField passwordField;

    public LoginPage() {
        super("Авторизация");

        initComponents();

        // Установка параметров окна
        setSize(350, 150);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Центрировать окно по центру экрана
        setVisible(true);
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(5, 10, 5, 10); // Отступы для компонентов

        // Добавление поля для логина
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.anchor = GridBagConstraints.EAST;
        panel.add(new JLabel("Логин:"), constraints);

        constraints.gridx = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        loginField = new JTextField(20); // Ширина поля для логина
        panel.add(loginField, constraints);

        // Добавление поля для пароля
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.anchor = GridBagConstraints.EAST;
        panel.add(new JLabel("Пароль:"), constraints);

        constraints.gridx = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        passwordField = new JPasswordField(20); // Ширина поля для пароля
        panel.add(passwordField, constraints);

        // Добавление кнопок
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.gridwidth = 2;
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.fill = GridBagConstraints.NONE;

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        JButton registerButton = new JButton("Регистрация");
        registerButton.setPreferredSize(new Dimension(120, 30));
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new RegisterPage();
            }
        });
        buttonPanel.add(registerButton);

        JButton loginButton = new JButton("Вход");
        loginButton.setPreferredSize(new Dimension(80, 30));
        loginButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String login = loginField.getText();
                String password = new String(passwordField.getPassword());

                Session session = HibernateUtil.getSession();

                // Получение списка всех пользователей из таблицы
                String hql = "FROM User WHERE login = :login AND password = :password";
                List<User> userList = session.createQuery(hql, User.class)
                        .setParameter("login", login)
                        .setParameter("password", password)
                        .list();

                // Вывод результатов запроса
                if (!userList.isEmpty()) {
                    System.out.println("Найденные пользователи:");
                    if (userList.size() == 1) {
                        if (userList.get(0).getRole().equalsIgnoreCase("admin")) {
                            dispose();
                            new AdminWindow();
                        }
                        if (userList.get(0).getRole().equalsIgnoreCase("user")) {
                            dispose();
                            new UserWindow(userList.get(0));
                        }
                    } else {
                        System.out.println("Найдено более 1 пользователя");
                    }
                } else {
                    System.out.println("Пользователи с именем '" + login + "' не найдены.");
                }

                // Завершение транзакции
                session.getTransaction().commit();
                session.close();
                JOptionPane.showMessageDialog(LoginPage.this, "Вы пытаетесь войти...");
            }
        });
        buttonPanel.add(loginButton);

        panel.add(buttonPanel, constraints);

        // Добавление панели на окно
        add(panel);
    }
}
