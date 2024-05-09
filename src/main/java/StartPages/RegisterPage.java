package StartPages;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RegisterPage extends JFrame {

    private JTextField loginField;
    private JPasswordField passwordField;
    private JTextField roleField;

    public RegisterPage() {
        super("Регистрация");
        initComponents();

        // Установка параметров окна
        setSize(400, 220); // Увеличим ширину для добавления кнопки "Назад"
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Закрывать только текущее окно
        setLocationRelativeTo(null); // Центрировать окно по центру экрана
        setVisible(true);
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(5, 10, 5, 10); // Отступы

        // Добавление компонентов на панель
        JLabel loginLabel = new JLabel("Логин:");
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.anchor = GridBagConstraints.LINE_END;
        panel.add(loginLabel, constraints);

        loginField = new JTextField(20); // Установка размера поля
        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.anchor = GridBagConstraints.LINE_START;
        panel.add(loginField, constraints);

        JLabel passwordLabel = new JLabel("Пароль:");
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.anchor = GridBagConstraints.LINE_END;
        panel.add(passwordLabel, constraints);

        passwordField = new JPasswordField(20); // Установка размера поля
        constraints.gridx = 1;
        constraints.gridy = 1;
        constraints.anchor = GridBagConstraints.LINE_START;
        panel.add(passwordField, constraints);

        JLabel roleLabel = new JLabel("Роль:");
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.anchor = GridBagConstraints.LINE_END;
        panel.add(roleLabel, constraints);

        roleField = new JTextField(20); // Установка размера поля
        constraints.gridx = 1;
        constraints.gridy = 2;
        constraints.anchor = GridBagConstraints.LINE_START;
        panel.add(roleField, constraints);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT)); // Панель для кнопок
        JButton backButton = new JButton("Назад");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // Закрыть текущее окно
                new LoginPage(); // Открыть окно авторизации
            }
        });
        buttonPanel.add(backButton); // Добавляем кнопку "Назад" на панель

        JButton registerButton = new JButton("Зарегистрироваться");
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Обработка регистрации
                String login = loginField.getText();
                String password = new String(passwordField.getPassword());
                String role = roleField.getText();
                // Добавьте здесь код для регистрации пользователя
                JOptionPane.showMessageDialog(RegisterPage.this, "Вы зарегистрированы!");
            }
        });
        buttonPanel.add(registerButton); // Добавляем кнопку "Зарегистрироваться" на панель

        constraints.gridx = 1;
        constraints.gridy = 3;
        constraints.anchor = GridBagConstraints.CENTER;
        panel.add(buttonPanel, constraints); // Добавляем панель с кнопками на основную панель

        // Добавление панели на окно
        add(panel);
    }
}
