/**
 * Enum for playing card Suit.
 */

public enum Suit {
    SPADES(4),
    HEARTS(3),
    DIAMONDS(2),
    CLUBS(1),
    NULL_SUIT(0);

    public static final String SUITS = "?cdhs";
    private final int suit;

    /**
     * Suit constructor.
     * @param suit The suit.
     */
    Suit(int suit) {
        this.suit = suit;
    }

    /**
     * Gets the suit.
     * @return suit
     */
    public int getSuit() {
        return this.suit;
    }
}
