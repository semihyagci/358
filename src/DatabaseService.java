import org.sqlite.SQLiteDataSource;

import java.sql.*;
import java.util.ArrayList;

public class
DatabaseService {

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
        String query = "INSERT INTO Players (username) VALUES (?);";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, userName);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void recordPlayMovement(String userName, String playedCard) {
        String query = "INSERT INTO Moves (playername, cardname) VALUES (?, ?);";

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
            String query = "DELETE FROM Players;";
            statement.executeUpdate(query);

            String query2 = "DELETE FROM Moves;";
            statement.executeUpdate(query2);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            connection.close();
        }
    }

    public static ArrayList<String> retrieveAllRecordsFromPlayedCardsTable() {
        String query = "SELECT * FROM Moves;";
        ArrayList<String> playedCardList = new ArrayList<>();

        try (PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                String playedCard = resultSet.getString("cardname");
                playedCardList.add(playedCard);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return playedCardList;
    }

}
