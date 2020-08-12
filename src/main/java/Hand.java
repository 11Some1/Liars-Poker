import java.util.*;

public class Hand implements Comparable<Hand>{
    private Card[] hand;
    private int power;

    /**
     * Creates a new hand from the given string.
     * A hand can consist of anywhere from one to five Cards.
     * A newly created Hand is automatically sorted to proper Poker format.
     * @param string the string to create the hand from, such as "Kd 5s Jc Ah Qc"
     * @see Card
     */
    Hand(String string) {
        final String[] parts = string.split(" ");
        if(parts.length > 5) throw new IllegalArgumentException("There cannot be more than 5 cards in a hand.");

        this.hand = new Card[parts.length];
        int index = 0;
        for (String part : parts) this.hand[index++] = new Card(part);

        power = check(hand);
    }

    /**
     * Creates a new hand from the given Card array.
     * A hand can consist of anywhere from one to five Cards.
     * A newly created Hand is automatically sorted to proper Poker format.
     * @param arr The array of Cards to convert to a Hand.
     * @see Card
     */
    Hand(Card[] arr) {
        if(arr.length > 5) throw new IllegalArgumentException("There cannot be more than 5 cards in a hand.");

        this.hand = new Card[arr.length];
        int index = 0;
        for (Card c : arr) this.hand[index++] = new Card(c);

        power = check(hand);
    }

    /**
     * Gets the current hand.
     * @return an array of Cards representing the current hand.
     */
    public Card[] getHand() {
        return this.hand;
    }

    /**
     * Gets the power of the Hand. The power of the Hand determines its Power ranking.
     * @return an integer array representing the Hand's Poker ranking.
     */
    public int getPower() {
        return this.power;
    }

    /**
     * Compares two Hands based on their Poker ranking.
     * @param o The other Hand object to compare to.
     * @return 1 if this Hand is better than the other Hand, -1 if this Hand is worse than the other Hand, 0 if equal
     */
    @Override
    public int compareTo(Hand o) {
        if(this.power > o.getPower()) return 1;
        else if(this.power < o.getPower()) return -1;
        else {
            for(int i = 0; i < 5; i++) {
                if(i >= this.hand.length) {
                    return -1;
                }
                else if(i >= o.getHand().length) {
                    return 1;
                }
                if(this.hand[i].compareTo(o.getHand()[i]) >= 1) return 1;
                else if(this.hand[i].compareTo(o.getHand()[i]) <= -1) return -1;
            }
        }
        return 0;
    }

    /**
     * Determines the PokerRank of the Card array, and sorts the Card array to proper Poker format.
     * @param arr The Card array in question.
     * @return An integer indicating the hand's PokerRank.
     * @see PokerRank
     */
    private static int check(Card[] arr) {
        Arrays.sort(arr, Collections.reverseOrder());
        if(isStraightFlush(arr)) return PokerRank.STRAIGHT_FLUSH.getRank();
        if(isFourKind(arr)) return PokerRank.FOUR_KIND.getRank();
        if(isFullHouse(arr)) return PokerRank.FULL_HOUSE.getRank();
        if(isFlush(arr)) return PokerRank.FLUSH.getRank();
        if(isStraight(arr)) return PokerRank.STRAIGHT.getRank();
        if(isThreeKind(arr)) return PokerRank.THREE_KIND.getRank();
        if(isTwoPair(arr)) return PokerRank.TWO_PAIR.getRank();
        if(isOnePair(arr)) return PokerRank.ONE_PAIR.getRank();
        return PokerRank.HIGH_CARD.getRank();
    }

    /**
     * Determines if the Card array is a straight flush.
     * This method assumes that arr is sorted from highest rank to lowest (done through Arrays.sort(arr, Collections.reverseOrder())).
     * @param arr The Card array in question.
     * @return true if the Card array is a straight flush; false otherwise.
     */
    private static boolean isStraightFlush(Card[] arr) {
        return isFlush(arr) && isStraight(arr);
    }

    /**
     * Determines if the Card array is a four of a kind.
     * If is a four of a kind, the Card array contents are sorted so that the kicker is last.
     * This method assumes that arr is sorted from highest rank to lowest (done through Arrays.sort(arr, Collections.reverseOrder())).
     * @param arr The Card array in question.
     * @return true if the Card array is a four of a kind; false otherwise.
     */
    private static boolean isFourKind(Card[] arr) {
        if(arr.length < 4) return false;
        if(arr[0].getRank() == arr[1].getRank() && arr[1].getRank() == arr[2].getRank() && arr[2].getRank() == arr[3].getRank()) {
            // x x x x, x x x x y
            return true;
        }
        else if(arr.length == 4) { // Equivalent to (!a1 && arr.length == 4)
            return false;
        }

        if(arr[1].getRank() == arr[2].getRank() && arr[2].getRank() == arr[3].getRank() && arr[3].getRank() == arr[4].getRank()) {
            // y x x x x
            Card c0 = arr[0], c1 = arr[1], c2 = arr[2], c3 = arr[3], c4 = arr[4];
            arr[0] = c1;
            arr[1] = c2;
            arr[2] = c3;
            arr[3] = c4;
            arr[4] = c0;
            return true;
        }
        return false;
    }

    /**
     * Determines if the Card array is a full house.
     * If it is a full house, the Card array contents are sorted so that it is in "3 over 2" format.
     * This method assumes that arr is sorted from highest rank to lowest (done through Arrays.sort(arr, Collections.reverseOrder())).
     * @param arr The Card array in question.
     * @return true if the Card array is a full house; false otherwise.
     */
    private static boolean isFullHouse(Card[] arr) {
        if(arr.length != 5) return false;

        if(arr[0].getRank() == arr[1].getRank() && arr[2].getRank() == arr[3].getRank() && arr[3].getRank() == arr[4].getRank()) {  // y y x x x
            Card c0 = arr[0], c1 = arr[1], c2 = arr[2], c3 = arr[3], c4 = arr[4];
            arr[0] = c2;
            arr[1] = c3;
            arr[2] = c4;
            arr[3] = c0;
            arr[4] = c1;
            return true;
        }
        return(arr[0].getRank() == arr[1].getRank() && arr[1].getRank() == arr[2].getRank() && arr[3].getRank() == arr[4].getRank()); // x x x y y
    }

    /**
     * Determines if the Card array is a flush.
     * This method assumes that arr is sorted from highest rank to lowest (done through Arrays.sort(arr, Collections.reverseOrder())).
     * @param arr The Card array in question.
     * @return true if the Card array is a flush; false otherwise.
     */
    private static boolean isFlush(Card[] arr) {
        if(arr.length != 5) return false;
        for(Card c : arr) {
            if(c.getSuit() == Suit.NULL_SUIT.getSuit()) { // Cannot make a flush out of null suits.
                return false;
            }
        }
        return arr[0].suitEquals(arr[1]) && arr[1].suitEquals(arr[2]) && arr[2].suitEquals(arr[3]) && arr[3].suitEquals(arr[4]);
    }

    /**
     * Determines if the Card array is a straight.
     * This method assumes that arr is sorted from highest rank to lowest (done through Arrays.sort(arr, Collections.reverseOrder())).
     * @param arr The Card array in question.
     * @return true if the Card array is a straight; false otherwise.
     */
    private static boolean isStraight(Card[] arr) {
        if(arr.length != 5) return false;
        if(arr[0].getRank() == Rank.ACE.getRank()) { // Wrap around straight
            boolean a1 = arr[1].getRank() == Rank.FIVE.getRank() && arr[2].getRank() == Rank.FOUR.getRank()
                    && arr[3].getRank() == Rank.THREE.getRank() && arr[4].getRank() == Rank.TWO.getRank(),
                    a2 = arr[1].getRank() == Rank.KING.getRank() && arr[2].getRank() == Rank.QUEEN.getRank()
                            && arr[3].getRank() == Rank.JACK.getRank() && arr[4].getRank() == Rank.TEN.getRank();
            return(a1 || a2);
        }
        else {
            int tgt = arr[0].getRank();
            for(int i = 1; i < 5; i++) {
                tgt--;
                if(arr[i].getRank() != tgt) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Determines if the Card array is a three of a kind.
     * If is a three of a kind, the Card array contents are sorted so that the kickers are last.
     * This method assumes that arr is sorted from highest rank to lowest (done through Arrays.sort(arr, Collections.reverseOrder())).
     * @param arr The Card array in question.
     * @return true if the Card array is a three of a kind; false otherwise.
     */
    private static boolean isThreeKind(Card[] arr) {
        // Do not need to check isFourKind or isFullHouse, since evaluation of this method should come after those ones
        if(arr.length < 3) return false;
        if(arr[0].getRank() == arr[1].getRank() && arr[1].getRank() == arr[2].getRank()) return true; // x x x, x x x y, x x x y z
        else if(arr.length == 3) return false;
        if(arr[1].getRank() == arr[2].getRank() && arr[2].getRank() == arr[3].getRank()) {
            if(arr.length == 4) { // y x x x
                Card c0 = arr[0], c1 = arr[1], c2 = arr[2], c3 = arr[3];
                arr[0] = c1;
                arr[1] = c2;
                arr[2] = c3;
                arr[3] = c0;
            }
            else if(arr.length == 5) { // y x x x z
                Card c0 = arr[0], c1 = arr[1], c2 = arr[2], c3 = arr[3], c4 = arr[4];
                arr[0] = c1;
                arr[1] = c2;
                arr[2] = c3;
                arr[3] = c0;
                arr[4] = c4;
            }
            return true;
        }
        else if(arr.length == 4) return false;
        if(arr[2].getRank() == arr[3].getRank() && arr[3].getRank() == arr[4].getRank()) { // y z x x x
            Card c0 = arr[0], c1 = arr[1], c2 = arr[2], c3 = arr[3], c4 = arr[4];
            arr[0] = c2;
            arr[1] = c3;
            arr[2] = c4;
            arr[3] = c0;
            arr[4] = c1;
            return true;
        }
        return false;
    }

    /**
     * Determines if the Card array is a two pair.
     * If is a two pair, the Card array contents are sorted so that the the kicker is last.
     * This method assumes that arr is sorted from highest rank to lowest (done through Arrays.sort(arr, Collections.reverseOrder())).
     * @param arr The Card array in question.
     * @return true if the Card array is a two pair; false otherwise.
     */
    private static boolean isTwoPair(Card[] arr) {
        // Do not need to check isFourKind or isFullHouse or isThreeKind, since evaluation of this method should come after those ones
        if(arr.length < 4) return false;
        if(arr[0].getRank() == arr[1].getRank() && arr[2].getRank() == arr[3].getRank()) { // x x y y, x x y y z
            return true;
        }
        else if(arr.length == 4) return false;
        if(arr[0].getRank() == arr[1].getRank() && arr[3].getRank() == arr[4].getRank()) { // x x z y y
            Card c2 = arr[2], c3 = arr[3], c4 = arr[4];
            arr[2] = c3;
            arr[3] = c4;
            arr[4] = c2;
            return true;
        }
        else if(arr[1].getRank() == arr[2].getRank() && arr[3].getRank() == arr[4].getRank()) { // z x x y y
            Card c0 = arr[0], c1 = arr[1], c2 = arr[2], c3 = arr[3], c4 = arr[4];
            arr[0] = c1;
            arr[1] = c2;
            arr[2] = c3;
            arr[3] = c4;
            arr[4] = c0;
            return true;
        }
        return false;
    }

    /**
     * Determines if the Card array is a one pair.
     * If is a one pair, the Card array contents are sorted so that the the kickers are last.
     * This method assumes that arr is sorted from highest rank to lowest (done through Arrays.sort(arr, Collections.reverseOrder())).
     * @param arr The Card array in question.
     * @return true if the Card array is a one pair; false otherwise.
     */
    private static boolean isOnePair(Card[] arr) {
        if(arr.length < 2) return false;
        if(arr[0].getRank() == arr[1].getRank()) return true; // x x, x x w, x x w y, x x w y z
        else if(arr.length == 2) return false;
        if(arr[1].getRank() == arr[2].getRank()) { // w x x, w x x y, w x x y z
            Card c0 = arr[0], c1 = arr[1], c2 = arr[2];
            arr[0] = c1;
            arr[1] = c2;
            arr[2] = c0;
            return true;
        }
        else if(arr.length == 3) return false;
        if(arr[2].getRank() == arr[3].getRank()) { // w y x x, w y x x z
            Card c0 = arr[0], c1 = arr[1], c2 = arr[2], c3 = arr[3];
            arr[0] = c2;
            arr[1] = c3;
            arr[2] = c0;
            arr[3] = c1;
            return true;
        }
        else if(arr.length == 4) return false;
        if(arr[3].getRank() == arr[4].getRank()) { // w y z x x
            Card c0 = arr[0], c1 = arr[1], c2 = arr[2], c3 = arr[3], c4 = arr[4];
            arr[0] = c3;
            arr[1] = c4;
            arr[2] = c0;
            arr[3] = c1;
            arr[4] = c2;
            return true;
        }
        return false;
    }
}
