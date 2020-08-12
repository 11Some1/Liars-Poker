/**
 * Enum for Poker hand rankings.
 */

public enum PokerRank {
    STRAIGHT_FLUSH(8),
    FOUR_KIND(7),
    FULL_HOUSE(6),
    FLUSH(5),
    STRAIGHT(4),
    THREE_KIND(3),
    TWO_PAIR(2),
    ONE_PAIR (1),
    HIGH_CARD(0);

    private final int rank;

    /**
     * PokerRank constructor.
     * @param rank The rank of the hand. The higher the rank, the better it is.
     */
    PokerRank(int rank) {
        this.rank = rank;
    }

    /**
     * Gets the PokerRank rank.
     * @return The PokerRank rank.
     */
    public int getRank() {
        return this.rank;
    }
}
