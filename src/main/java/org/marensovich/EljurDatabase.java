package org.marensovich;

import java.sql.*;

public class EljurDatabase {

    private Connection connection;

    public void connect() {
        String URL = "jdbc:mariadb://199.83.103.127:3306/s12_kipfin";
        String USER = "u12_j9WEESQaBN";
        String PASSWORD = "gnPguP!Z@+W^w2TEXrztS32y";

        try {
            Class.forName("org.mariadb.jdbc.Driver");
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Подключение к базе данных успешно установлено.");
        } catch (SQLException e) {
            System.err.println("Ошибка при подключении к базе данных: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.err.println("Драйвер MariaDB/MySQL не найден: " + e.getMessage());
        }
    }


    public void createUserTableIfNotExist() {
        String sql = "CREATE TABLE IF NOT EXISTS telegram_users (\n"
                + " user_id INT NOT NULL,\n"
                + " telegram_id INT NOT NULL,\n"
                + " username VARCHAR(255) NOT NULL,\n"
                + " fullName VARCHAR(255) NOT NULL\n"
                + ");";

        connect();
        if (connection == null) {
            System.err.println("Соединение с базой данных не установлено.");
            return;
        }

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
            System.out.println("Таблица успешно создана или уже существует.");
        } catch (SQLException e) {
            System.err.println("Ошибка при создании таблицы: " + e.getMessage());
        } finally {
            closeConnection();
        }
    }

    public void checkDatabase(){
        createUserTableIfNotExist();
    }

    public boolean logoutUser(Long telegramId) {
        String sql = "DELETE FROM telegram_users WHERE telegram_id = ?";

        connect();
        if (connection == null) {
            System.err.println("Соединение с базой данных не установлено.");
            return false;
        }

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, telegramId);
            int rowsDeleted = pstmt.executeUpdate();
            return rowsDeleted > 0;
        } catch (SQLException e) {
            System.err.println("Ошибка при выходе пользователя: " + e.getMessage());
        } finally {
            closeConnection();
        }

        return false;
    }

    public boolean isUserAuthorized(Long telegramId) {
        String sql = "SELECT COUNT(*) FROM telegram_users WHERE telegram_id = ?";

        connect();
        if (connection == null) {
            System.err.println("Соединение с базой данных не установлено.");
            return false;
        }

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, telegramId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при проверке авторизации пользователя: " + e.getMessage());
        } finally {
            closeConnection();
        }

        return false;
    }
    public Integer checkUserCredentials(String username, String password, String phone) {
        String sql = "SELECT user_id FROM user WHERE user_username = ? AND user_password = ? AND user_phone = ?";

        connect();
        if (connection == null) {
            System.err.println("Соединение с базой данных не установлено.");
            return null;
        }

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, phone);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("user_id"); // Возвращаем userID
                } else {
                    return null; // Данные не найдены
                }
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при проверке данных пользователя: " + e.getMessage());
            return null;
        } finally {
            closeConnection();
        }
    }

    public void addAuthUser(Integer userID, Long telegram_id, String username, String fullName) {
        String sql = "INSERT INTO telegram_users(user_ID, telegram_id, username, fullName) VALUES(?, ?, ?, ?)";

        connect();
        if (connection == null) {
            System.err.println("Соединение с базой данных не установлено.");
            return;
        }

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userID);
            pstmt.setLong(2, telegram_id);
            pstmt.setString(3, username);
            pstmt.setString(4, fullName);

            pstmt.executeUpdate();
            System.out.println("Данные пользователя успешно сохранены.");
        } catch (SQLException e) {
            System.err.println("Ошибка при сохранении данных пользователя: " + e.getMessage());
        } finally {
            closeConnection();
        }
    }

    public void clearUserTelegramID(Integer userID) {
        String sql = "UPDATE user SET user_telegramid = NULL WHERE user_id = ?";

        connect();
        if (connection == null) {
            System.err.println("Соединение с базой данных не установлено.");
            return;
        }

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userID); // Устанавливаем userID для условия

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Телеграм ID пользователя успешно очищен.");
            } else {
                System.out.println("Обновление не выполнено. Пользователь с ID " + userID + " не найден.");
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при очистке данных пользователя: " + e.getMessage());
        } finally {
            closeConnection();
        }
    }


    public void updateUserTelegramID(Integer userID, Long telegramId) {
        String sql = "UPDATE user SET user_telegramid = ? WHERE user_id = ?";

        connect();
        if (connection == null) {
            System.err.println("Соединение с базой данных не установлено.");
            return;
        }

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, telegramId);
            pstmt.setInt(2, userID);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Данные пользователя успешно обновлены.");
            } else {
                System.out.println("Обновление не выполнено. Пользователь с ID " + userID + " не найден.");
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при сохранении данных пользователя: " + e.getMessage());
        } finally {
            closeConnection();
        }
    }

    public String getUserFullName(Integer userID) {
        String sql = "SELECT user_full_name FROM user WHERE user_id = ?";

        connect();
        if (connection == null) {
            System.err.println("Соединение с базой данных не установлено.");
            return null; // Возвращаем null вместо sql
        }

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userID);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("user_full_name"); // Возвращаем полное имя
                } else {
                    return null; // Данные не найдены
                }
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при получении полного имени пользователя: " + e.getMessage());
            return null; // Возвращаем null в случае ошибки
        } finally {
            closeConnection();
        }
    }

    public Integer getUserID(Long telegramId) {
        String sql = "SELECT user_id FROM telegram_users WHERE telegram_id = ?";

        connect();
        if (connection == null) {
            System.err.println("Соединение с базой данных не установлено.");
            return null; // Возвращаем null вместо sql
        }

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, telegramId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("user_id"); // Возвращаем полное имя
                } else {
                    return null; // Данные не найдены
                }
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при получении полного имени пользователя: " + e.getMessage());
            return null; // Возвращаем null в случае ошибки
        } finally {
            closeConnection();
        }
    }





    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Подключение к базе данных закрыто.");
            } catch (SQLException e) {
                System.err.println("Ошибка при закрытии подключения:");
                e.printStackTrace();
            }
        }
    }
}