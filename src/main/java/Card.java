import java.util.*;

/**
 * An immutable class representing a card from a normal 52-card deck.
 */
public class Card implements Comparable<Card> {

    private int rank;
    private int suit;

    /**
     * Card constructor.
     * @param string A string of length 2, with the first character being the rank and the second character being the suit.
     * @see Rank#RANKS
     * @see Suit#SUITS
     */
    public Card(String string) {
        if (string == null || string.length() != 2) {
            throw new IllegalArgumentException("Card string must be non-null with length of exactly 2.");
        }
        rank = Rank.RANKS.indexOf(string.charAt(0)) + 2;
        suit = Suit.SUITS.indexOf(string.charAt(1));
        if(!isValidRank(rank)) {
            throw new IllegalArgumentException("Invalid rank.");
        }
        if(!isValidSuit(suit)) {
            throw new IllegalArgumentException("Invalid suit.");
        }
    }

    /**
     * Card copy constructor.
     * @param c The Card to copy.
     */
    public Card(Card c) {
        rank = c.getRank();
        suit = c.getSuit();
    }

    /**
     * Returns whether the given rank is valid or not.
     * @param rank rank to check.
     * @return true if the rank is valid, false otherwise.
     */
    private static boolean isValidRank(int rank) {
        return rank >= Rank.TWO.getRank() && rank <= Rank.ACE.getRank();
    }

    /**
     * Returns whether the given suit is valid or not.
     * @param suit suit to check.
     * @return true if the suit is valid, false otherwise.
     */
    private static boolean isValidSuit(int suit) {
        return suit == Suit.CLUBS.getSuit() || suit == Suit.DIAMONDS.getSuit() || suit == Suit.HEARTS.getSuit()
                || suit == Suit.SPADES.getSuit() || suit == Suit.NULL_SUIT.getSuit();
    }

    /**
     * Returns the rank of the card.
     * @return rank of the card as an integer
     * @see Rank#TWO
     * @see Rank#THREE
     * @see Rank#FOUR
     * @see Rank#FIVE
     * @see Rank#SIX
     * @see Rank#SEVEN
     * @see Rank#EIGHT
     * @see Rank#NINE
     * @see Rank#TEN
     * @see Rank#JACK
     * @see Rank#QUEEN
     * @see Rank#KING
     * @see Rank#ACE
     */
    public int getRank() {
        return(rank);
    }

    /**
     * Returns the suit of the card.
     * @return Suit of the card as an integer.
     * @see Suit#SPADES
     * @see Suit#HEARTS
     * @see Suit#DIAMONDS
     * @see Suit#CLUBS
     * @see Suit#NULL_SUIT
     */
    public int getSuit() {
        return(suit);
    }

    /**
     * Determines if two cards are equal.
     * Two cards are equal if they have the same rank, and the two suit parts are equal or at least ne of the two is a NULL_SUIT.
     * @param o The other Card object to compare to.
     * @return True if the two cards are equal; false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        Card ob = (Card) o;
        return this.getRank() == ob.getRank() && this.suitEquals(ob);
    }

    /**
     * Determines if two cards' suit parts are equal.
     * Two cards have equal suit parts if they are equal, or at least one of the two is a NULL_SUIT.
     * @param o The other Card object to compare to.
     * @return True if the two cards' suit parts are equal; false otherwise.
     */
    public boolean suitEquals(Card o) {
        return this.getSuit() == o.getSuit() || this.getSuit() == Suit.NULL_SUIT.getSuit() || o.getSuit() == Suit.NULL_SUIT.getSuit();
    }

    /**
     * Returns a string representation of the card.
     * For example, the king of spades is "Ks", and the jack of hearts is "Jh".
     * @return a string representation of the card.
     */
    @Override
    public String toString() {
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
            if(this.getSuit() > o.getSuit()) return 1;
            else if(this.getSuit() < o.getSuit()) return -1;
        }
        return 0;
    }

    /**
     * Returns an address corresponding to the Discord emote representation of the card's rank.
     * @return a string representing a Discord emote address.
     */
    public String rankImg() {
        if(suit == Suit.CLUBS.getSuit() || suit == Suit.SPADES.getSuit() || suit == Suit.NULL_SUIT.getSuit()) {
            if(rank == Rank.TWO.getRank()) {
                return ImgAddress.b2.getAddress();
            }
            else if(rank == Rank.THREE.getRank()) {
                return ImgAddress.b3.getAddress();
            }
            else if(rank == Rank.FOUR.getRank()) {
                return ImgAddress.b4.getAddress();
            }
            else if(rank == Rank.FIVE.getRank()) {
                return ImgAddress.b5.getAddress();
            }
            else if(rank == Rank.SIX.getRank()) {
                return ImgAddress.b6.getAddress();
            }
            else if(rank == Rank.SEVEN.getRank()) {
                return ImgAddress.b7.getAddress();
            }
            else if(rank == Rank.EIGHT.getRank()) {
                return ImgAddress.b8.getAddress();
            }
            else if(rank == Rank.NINE.getRank()) {
                return ImgAddress.b9.getAddress();
            }
            else if(rank == Rank.TEN.getRank()) {
                return ImgAddress.bT.getAddress();
            }
            else if(rank == Rank.JACK.getRank()) {
                return ImgAddress.bJ.getAddress();
            }
            else if(rank == Rank.QUEEN.getRank()) {
                return ImgAddress.bQ.getAddress();
            }
            else if(rank == Rank.KING.getRank()) {
                return ImgAddress.bK.getAddress();
            }
            else if(rank == Rank.ACE.getRank()) {
                return ImgAddress.bA.getAddress();
            }
        }
        else if(suit == Suit.DIAMONDS.getSuit() || suit == Suit.HEARTS.getSuit()) {
            if(rank == Rank.TWO.getRank()) {
                return ImgAddress.r2.getAddress();
            }
            else if(rank == Rank.THREE.getRank()) {
                return ImgAddress.r3.getAddress();
            }
            else if(rank == Rank.FOUR.getRank()) {
                return ImgAddress.r4.getAddress();
            }
            else if(rank == Rank.FIVE.getRank()) {
                return ImgAddress.r5.getAddress();
            }
            else if(rank == Rank.SIX.getRank()) {
                return ImgAddress.r6.getAddress();
            }
            else if(rank == Rank.SEVEN.getRank()) {
                return ImgAddress.r7.getAddress();
            }
            else if(rank == Rank.EIGHT.getRank()) {
                return ImgAddress.r8.getAddress();
            }
            else if(rank == Rank.NINE.getRank()) {
                return ImgAddress.r9.getAddress();
            }
            else if(rank == Rank.TEN.getRank()) {
                return ImgAddress.rT.getAddress();
            }
            else if(rank == Rank.JACK.getRank()) {
                return ImgAddress.rJ.getAddress();
            }
            else if(rank == Rank.QUEEN.getRank()) {
                return ImgAddress.rQ.getAddress();
            }
            else if(rank == Rank.KING.getRank()) {
                return ImgAddress.rK.getAddress();
            }
            else if(rank == Rank.ACE.getRank()) {
                return ImgAddress.rA.getAddress();
            }
        }
        return "???";
    }

    /**
     * Returns an address corresponding to the Discord emote representation of the card's suit.
     * @return a string representing a Discord emote address.
     */
    public String suitImg() {
        if(suit == Suit.SPADES.getSuit()) {
            return ImgAddress.sS.getAddress();
        }
        else if(suit == Suit.HEARTS.getSuit()) {
            return ImgAddress.sH.getAddress();
        }
        else if(suit == Suit.DIAMONDS.getSuit()) {
            return ImgAddress.sD.getAddress();
        }
        else if(suit == Suit.CLUBS.getSuit()) {
            return ImgAddress.sC.getAddress();
        }
        else if(suit == Suit.NULL_SUIT.getSuit()) {
            return ImgAddress.sN.getAddress(); //TODO: Update README.md
        }
        return "???";
    }

    /**
     * Converts the given array of Cards into concatenation of their string representations,
     * that will display as a Discord emote.
     * @param arr The array of Cards in question.
     * @return a concatenation of the string representations of the given cards
     */
    public static String getEmotes(Card[] arr) {
        StringBuilder builder = new StringBuilder();
        for(int j = 0; j < arr.length / 10 + 1; j++) {
            StringBuilder rk = new StringBuilder();
            StringBuilder st = new StringBuilder();
            for(int i = j * 10; i < (j+1) * 10 && i < arr.length; i++) {
                rk.append(arr[i].rankImg() + " ");
                st.append(arr[i].suitImg() + " ");
            }
            builder.append(rk.toString() + "\n" + st.toString());
            if(j < arr.length / 10) {
                builder.append("\n");
            }
        }
        return builder.toString();
    }
}
