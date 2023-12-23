import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {
    private Player player;

    @BeforeEach
    void setUp() {
        player = new Player();
    }

    @Test
    public void testSetTurn() {
        player.setTurn(true);
        assertTrue(player.isTurn());

        player.setTurn(false);
        assertFalse(player.isTurn());
    }


    @Test
    void testGetWinCount() {
        player.setWinCount(3);
        assertEquals(3, player.getWinCount());
    }

    @Test
    void testGetName() {
        player.userName = "TestUser";
        assertEquals("TestUser", player.getName());
    }


    @Test
    void testCreateCard() {
        String cardName = "H10"; // Heart 10
        Card card = UtilityService.createCard(cardName);

        assertEquals("hearts", card.getSuit());
        assertEquals("10", card.getRank());
    }


    @Test
    void testInitialize() {
        // Assuming initialize method has UI components
        assertDoesNotThrow(() -> player.initialize());
        assertNotNull(player.getFrame());
    }


    @Test
    void testSetJoker() {
        player.getHand().add(new Card("hearts", "10"));
        player.setJoker("hearts");
        Card jokerCard = player.getHand().get(0);

        assertTrue(jokerCard.isJoker());
        assertEquals(23, jokerCard.getValue()); // Assuming increaseValue method increases rank
    }

    // Add more test methods as needed

}


class DatabaseServiceTest {

    private Player player;

    private DatabaseService dbService;

    @BeforeEach
    void setUp() {
        player = new Player();
        dbService = new DatabaseService();
    }

    @AfterEach
    void setDown(){
        try {
            DatabaseService.deleteGameRecordsAndTerminateConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testCreatePlayer() {
        // Create a DatabaseService instance with the test connection
        DatabaseService databaseService = new DatabaseService();

        // Test creating a player
        databaseService.createPlayer("TestUser");

        // Verify that the user was created by checking the database
        try {
            PreparedStatement statement = DatabaseService.connection.prepareStatement("SELECT * FROM UserTable WHERE userName = 'TestUser'");
            ResultSet st=statement.executeQuery();
            assertEquals("TestUser",st.getString(2));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testRecordPlayMovement() {
        // Create a DatabaseService instance with the test connection
        DatabaseService databaseService = new DatabaseService();

        // Test recording a play movement
        databaseService.recordPlayMovement("TestUser", "D4");

        // Verify that the play movement was recorded by checking the database
        try{
            PreparedStatement stmt=DatabaseService.connection.prepareStatement("SELECT * FROM PlayedCardsTable WHERE userName = 'TestUser' AND playedCard = 'D4'");
            ResultSet st=stmt.executeQuery();
            assertEquals("D4",st.getString(3));

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}


class GamePanelTest {

    @Test
    void testConstructor() throws IOException {
        ArrayList<Card> playerHand = new ArrayList<>();
        ArrayList<Card> onTable = new ArrayList<>();
        Player player = new Player();

        try {
            GamePanel gamePanel = new GamePanel(playerHand, onTable, player);

            // Check if the GamePanel is created successfully
            assertNotNull(gamePanel);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
class UtilityServiceTest {

    @Test
    void testCreateStandardDeck() {
        ArrayList<Card> cards = UtilityService.createStandardDeck(true);
        for (int i = 0; i < cards.size(); i++) {
            for (int j = i + 1; j < cards.size(); j++) {
                assertNotEquals(cards.get(i), cards.get(j));
            }
        }
        assertNotNull(cards);
        assertEquals(52, cards.size());

    }

    @Test
    void testFindKeyWithMaxValue() {
        HashMap<String, Card> hashMap = new HashMap<>();
        hashMap.put("Player1", new Card("hearts", "Ace"));
        hashMap.put("Player2", new Card("clubs", "King"));
        hashMap.put("Player3", new Card("diamonds", "10"));

        String maxKey = UtilityService.findKeyWithMaxValue(hashMap);

        assertEquals("Player1", maxKey);
    }

    @Test
    void testCreateCard() {
        Card card = UtilityService.createCard("CA");
        assertEquals("clubs", card.getSuit());
        assertEquals("Ace", card.getRank());
    }
}


