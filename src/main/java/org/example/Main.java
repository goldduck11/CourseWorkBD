package org.example;

import Connect.HibernateUtil;
import StartPages.LoginPage;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Создание и отображение окна авторизации
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                HibernateUtil.getSessionFactory();
                new LoginPage();
            }
        });
    }
}