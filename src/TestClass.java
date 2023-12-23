import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class TestClass {
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