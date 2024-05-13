package Connect;

import lombok.SneakyThrows;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class MyConnection {
    private static Connection connection = null;
    private static Statement statement = null;
    private static final String url = "jdbc:postgresql://localhost:5432/postgres";
    private static final String user = "postgres";
    private static final String password = "HjcnVjq182CV";

    // Метод для подключения к базе данных PostgreSQL
    @SneakyThrows
    public static Connection getConnection() {
        if (connection == null) {
            connection = DriverManager.getConnection(url, user, password);
        }
        System.out.println("Подключение к PostgreSQL установлено");
        return connection;
    }

    @SneakyThrows
    public static Statement getStatement() {
        if (statement == null) {
            statement = getConnection().createStatement();
        }
        return statement;
    }
}
