package Utils;

import Statuses.AuthStatus;
import Statuses.RegStatus;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;

public class DBService {

    private static Connection connection;
    private static Statement stmt;

    public static void connect() {
        try {
            Path path = Paths.get("./server/src/main/java/Utils/DBConnectionData.txt");
            String connectionString = Files.readAllLines(path).get(0);
            connection = DriverManager.getConnection(connectionString);
            stmt = connection.createStatement();
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    public static void disconnect() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean isRegLogin(String login) {
        String sql = String.format("SELECT * FROM [user] WHERE login = '%s'", login);
        try {
            ResultSet rs = stmt.executeQuery(sql);
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static RegStatus reg(String login, long password) {
        if (isRegLogin(login)) {
            return RegStatus.LOGIN_USED;
        }
        String sql = String.format("insert into [user] " +
                        "(login, password) " +
                        "values " +
                        "('%s', '%s');",
                login, password);
        try {
            if (stmt.executeUpdate(sql) != 0) {
                return RegStatus.OK;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return RegStatus.ERROR;
    }

    public static AuthStatus auth(String login, long password) {
        if (!isRegLogin(login)) {
            return AuthStatus.NO_LOGIN;
        }
        try {
            String sql = String.format("SELECT * FROM [user] WHERE login = '%s' and password = '%s'", login, password);
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                return AuthStatus.OK;
            } else {
                return AuthStatus.INCORRECT_PASSWORD;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return AuthStatus.ERROR;
    }

}
