import java.util.*;

public class Deck {
    private ArrayList<Card> deck;

    /**
     * Constructor for Deck. Makes a normal deck.
     */
    public Deck() {
        deck = new ArrayList<Card>();
        deck.add(Card.fromString("Ac"));
        deck.add(Card.fromString("2c"));
        deck.add(Card.fromString("3c"));
        deck.add(Card.fromString("4c"));
        deck.add(Card.fromString("5c"));
        deck.add(Card.fromString("6c"));
        deck.add(Card.fromString("7c"));
        deck.add(Card.fromString("8c"));
        deck.add(Card.fromString("9c"));
        deck.add(Card.fromString("Tc"));
        deck.add(Card.fromString("Jc"));
        deck.add(Card.fromString("Qc"));
        deck.add(Card.fromString("Kc"));
        deck.add(Card.fromString("Ad"));
        deck.add(Card.fromString("2d"));
        deck.add(Card.fromString("3d"));
        deck.add(Card.fromString("4d"));
        deck.add(Card.fromString("5d"));
        deck.add(Card.fromString("6d"));
        deck.add(Card.fromString("7d"));
        deck.add(Card.fromString("8d"));
        deck.add(Card.fromString("9d"));
        deck.add(Card.fromString("Td"));
        deck.add(Card.fromString("Jd"));
        deck.add(Card.fromString("Qd"));
        deck.add(Card.fromString("Kd"));
        deck.add(Card.fromString("Ah"));
        deck.add(Card.fromString("As"));
        deck.add(Card.fromString("2h"));
        deck.add(Card.fromString("3h"));
        deck.add(Card.fromString("4h"));
        deck.add(Card.fromString("5h"));
        deck.add(Card.fromString("6h"));
        deck.add(Card.fromString("7h"));
        deck.add(Card.fromString("8h"));
        deck.add(Card.fromString("9h"));
        deck.add(Card.fromString("Th"));
        deck.add(Card.fromString("Jh"));
        deck.add(Card.fromString("Qh"));
        deck.add(Card.fromString("Kh"));
        deck.add(Card.fromString("2s"));
        deck.add(Card.fromString("3s"));
        deck.add(Card.fromString("4s"));
        deck.add(Card.fromString("5s"));
        deck.add(Card.fromString("6s"));
        deck.add(Card.fromString("7s"));
        deck.add(Card.fromString("8s"));
        deck.add(Card.fromString("9s"));
        deck.add(Card.fromString("Ts"));
        deck.add(Card.fromString("Js"));
        deck.add(Card.fromString("Qs"));
        deck.add(Card.fromString("Ks"));
    }

    /**
     * Shuffle the deck. Randomly re-orders the cards.
     * @return void
     */
    public void shuffle() {
        Collections.shuffle(deck);
    }

    /**
     * Draw a Card from the deck. Removes from the "top" (beginning of ArrayList).
     * @return A Card object from the deck.
     */
    public Card draw() {
        return deck.remove(0);
    }

    /**
     * Get the ArrayList of Cards.
     * @return The Cards.
     */
    public ArrayList<Card> getDeck() {
        return deck;
    }
}
