import org.sqlite.SQLiteDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseService {

    static Connection connection;

    public DatabaseService() {
        try {
            SQLiteDataSource ds = new SQLiteDataSource();
            ds.setUrl("jdbc:sqlite:358.db");
            connection = ds.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void createPlayer(String userName) {
        String query = "INSERT INTO UserTable (userName) VALUES (?);";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, userName);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void recordPlayMovement(String userName, String playedCard) {
        String query = "INSERT INTO PlayedCardsTable (userName, playedCard) VALUES (?, ?);";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, userName);
            preparedStatement.setString(2, playedCard);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteGameRecordsAndTerminateConnection() throws SQLException {
        try (Statement statement = connection.createStatement()) {
            String query = "DELETE FROM UserTable;";
            statement.executeUpdate(query);

            String query2 = "DELETE FROM PlayedCardsTable;";
            statement.executeUpdate(query2);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            connection.close();
        }
    }

}
