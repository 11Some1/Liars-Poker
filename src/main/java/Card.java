/**
 * An immutable class representing a card from a normal 52-card deck.
 */
public class Card implements Comparable<Card> {
    private final int value;  // Format: xxxAKQJT 98765432 CDHSrrrr xxPPPPPP

    // Ranks
    public static final int DEUCE = 0;
    public static final int TREY = 1;
    public static final int FOUR = 2;
    public static final int FIVE = 3;
    public static final int SIX = 4;
    public static final int SEVEN = 5;
    public static final int EIGHT = 6;
    public static final int NINE = 7;
    public static final int TEN = 8;
    public static final int JACK = 9;
    public static final int QUEEN = 10;
    public static final int KING = 11;
    public static final int ACE = 12;

    // Suits
    public static final int CLUBS = 0x8000;
    public static final int DIAMONDS = 0x4000;
    public static final int HEARTS = 0x2000;
    public static final int SPADES = 0x1000;

    // Rank symbols
    private static final String RANKS = "23456789TJQKA";
    private static final String SUITS = "shdc";

    /**
     * Creates a new card with the given rank and suit.
     * @param rank the rank of the card, e.g. {@link Card#SIX}
     * @param suit the suit of the card, e.g. {@link Card#CLUBS}
     */
    public Card(int rank, int suit) {
        if (!isValidRank(rank)) {
            throw new IllegalArgumentException("Invalid rank.");
        }
        if (!isValidSuit(suit)) {
            throw new IllegalArgumentException("Invalid suit.");
        }
        value = (1 << (rank + 16)) | suit | (rank << 8) | Tables.PRIMES[rank];
    }

    /**
     * Create a new {@link Card} instance from the given string.
     * The string should be a two-character string where the first character
     * is the rank and the second character is the suit. For example, "Kc" means
     * the king of clubs, and "As" means the ace of spades.
     * @param string Card to create as a string.
     * @return a new {@link Card} instance corresponding to the given string.
     */
    public static Card fromString(String string) {
        if (string == null || string.length() != 2) {
            throw new IllegalArgumentException("Card string must be non-null with length of exactly 2.");
        }

        final int rank = RANKS.indexOf(string.charAt(0));
        final int suit = SPADES << SUITS.indexOf(string.charAt(1));

        return new Card(rank, suit);
    }

    /**
     * Returns the rank of the card.
     * @return rank of the card as an integer.
     * @see Card#ACE
     * @see Card#DEUCE
     * @see Card#TREY
     * @see Card#FOUR
     * @see Card#FIVE
     * @see Card#SIX
     * @see Card#SEVEN
     * @see Card#EIGHT
     * @see Card#NINE
     * @see Card#TEN
     * @see Card#JACK
     * @see Card#QUEEN
     * @see Card#KING
     */
    public int getRank() {
        return (value >> 8) & 0xF;
    }

    /**
     * Returns the suit of the card.
     * @return Suit of the card as an integer.
     * @see Card#SPADES
     * @see Card#HEARTS
     * @see Card#DIAMONDS
     * @see Card#CLUBS
     */
    public int getSuit() {
        return value & 0xF000;
    }

    /**
     * Determines if two cards are equal.
     * Two cards are equal if they have the same rank and suit.
     * @param o The other Card object to compare to.
     * @return True if the two cards are equal; false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        Card ob = (Card) o;
        return this.getRank() == ob.getRank() && this.getSuit() == ob.getSuit();
    }

    /**
     * Returns a string representation of the card.
     * For example, the king of spades is "Ks", and the jack of hearts is "Jh".
     * @return a string representation of the card.
     */
    @Override
    public String toString() {
        char rank = RANKS.charAt(getRank());
        char suit = SUITS.charAt((int) (Math.log(getSuit()) / Math.log(2)) - 12);
        return "" + rank + suit;
    }

    /**
     * Compares two Cards based on their Poker ranking.
     * @param o The other Card object to compare to.
     * @return 1 if this Card is better than the other Card, -1 if this Card is worse than the other Card, 0 if equal
     */
    public int compareTo(Card o){
        if(this.getRank() > o.getRank()) return 1;
        else if(this.getRank() < o.getRank()) return -1;
        else {
            if(this.getSuit() < o.getSuit()) return 1;
            else if(this.getSuit() > o.getSuit()) return -1;
        }
        return 0;
    }

    /**
     * Returns an address corresponding to the Discord emote representation of the card's rank.
     * @return a string representing a Discord emote address.
     */
    public String rankImg() {
        char rank = RANKS.charAt(getRank());
        char suit = SUITS.charAt((int) (Math.log(getSuit()) / Math.log(2)) - 12);
        if(suit == 'c' || suit == 's') {
            switch(rank) {
                case 'A':
                    return "<:bA:741730954774577213>";
                case '2':
                    return "<:b2:741730954820714538>";
                case '3':
                    return "<:b3:741730954992812193>";
                case '4':
                    return "<:b4:741730954644815884>";
                case '5':
                    return "<:b5:741730955089281064>";
                case '6':
                    return "<:b6:741730955055595640>";
                case '7':
                    return "<:b7:741730954741284946>";
                case '8':
                    return "<:b8:741730955135549470>";
                case '9':
                    return "<:b9:741730955181424660>";
                case 'T':
                    return "<:bT:741730955701780552>";
                case 'J':
                    return "<:bJ:741730955181555824>";
                case 'Q':
                    return "<:bQ:741730954900668438>";
                case 'K':
                    return "<:bK:741730955189813288>";
            }
        }
        else if(suit == 'd' || suit == 'h') {
            switch(rank) {
                case 'A':
                    return "<:rA:741730955101863937>";
                case '2':
                    return "<:r2:741730955806638080>";
                case '3':
                    return "<:r3:741730954955063358>";
                case '4':
                    return "<:r4:741730955512774777>";
                case '5':
                    return "<:r5:741730955475157042>";
                case '6':
                    return "<:r6:741730955265310761>";
                case '7':
                    return "<:r7:741730955538202704>";
                case '8':
                    return "<:r8:741730955420500078>";
                case '9':
                    return "<:r9:741730955387207872>";
                case 'T':
                    return "<:rT:741730955055595571>";
                case 'J':
                    return "<:rJ:741730955659837559>";
                case 'Q':
                    return "<:rQ:741730955580014643>";
                case 'K':
                    return "<:rK:741730955559043102>";
            }
        }
        return "???";
    }

    /**
     * Returns an address corresponding to the Discord emote representation of the card's suit.
     * @return a string representing a Discord emote address.
     */
    public String suitImg() {
        char suit = SUITS.charAt((int) (Math.log(getSuit()) / Math.log(2)) - 12);
        switch(suit) {
            case 'c':
                return "<:sC:741730955307253801>";
            case 's':
                return "<:sS:741730955772821514>";
            case 'd':
                return "<:sD:741730954904600716>";
            case 'h':
                return "<:sH:741730955265441792>";
        }
        return "???";
    }

    /**
     * Returns the value of the card as an integer.
     * The value is represented as the bits <code>xxxAKQJT 98765432 CDHSrrrr xxPPPPPP</code>,
     * where <code>x</code> means unused, <code>AKQJT 98765432</code> are bits turned on/off
     * depending on the rank of the card, <code>CDHS</code> are the bits corresponding to the
     * suit, and <code>PPPPPP</code> is the prime number of the card.
     * @return the value of the card.
     */
    int getValue() {
        return value;
    }

    /**
     * Returns whether the given rank is valid or not.
     * @param rank rank to check.
     * @return true if the rank is valid, false otherwise.
     */
    private static boolean isValidRank(int rank) {
        return rank >= DEUCE && rank <= ACE;
    }

    /**
     * Returns whether the given suit is valid or not.
     * @param suit suit to check.
     * @return true if the suit is valid, false otherwise.
     */
    private static boolean isValidSuit(int suit) {
        return suit == CLUBS || suit == DIAMONDS || suit == HEARTS || suit == SPADES;
    }
}
