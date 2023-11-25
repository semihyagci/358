import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Scanner;

public class Board {
    private final ArrayList<Player> players;
    private final ArrayList<Card> cards;
    Scanner input=new Scanner(System.in);

    public Board(){
        players=new ArrayList<>();
        cards=new ArrayList<>();
    }
    public void add(Player player){
        players.add(player);
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public void play(){
        Iterator<Player> it=players.iterator();
        for (Player player:players){
            while (player.getHand().isEmpty()){
                if (player.isTurn()){
                    System.out.println("Please play one of your cards.");
                    player.displayHand();
                    int index=input.nextInt();
                    Card card=player.getHand().remove(index-1);
                    cards.add(card);
                }
            }

        }
    }


}
