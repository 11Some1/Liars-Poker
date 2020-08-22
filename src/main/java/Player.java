import net.dv8tion.jda.api.entities.User;

public class Player {
    private User user;
    private static int startNumCards = 1; // TODO: Can edit
    private static int endNumCards = 4; // TODO: Can edit
    private int numCards;
    private Card[] cards;

    /**
     * Creates a new Player with the given Discord user.
     * @param user The Discord user that the Player represents.
     */
    Player(User user) {
        this.user = user;
        this.numCards = startNumCards;
        cards = new Card[numCards];
    }

    /**
     * Gets the Discord user associated with this player.
     * @return the Discord user associated with this player.
     */
    public User getUser() { return user; }

    /**
     * Gets the number of lives this Player has left.
     * @return the number of lives this Player has left.
     */
    public int getNumCards() { return numCards; }

    /**
     * Gets the array of Cards this player currently has.
     * @return the array of Cards this player currently has.
     */
    public Card[] getCards() { return cards; }

    /**
     * Assigns a Card c to index idx in the Player's array of cards.
     * @param c The Card to assign.
     * @param idx The index to assign the Card to.
     */
    public void assignCard(Card c, int idx) {
        cards[idx] = c;
    }

    /**
     * Determines if two Players are equal.
     * Two Players are equal if they have the same Discord user.
     * @param o The other Player object to compare to.
     * @return True if the two Players are equal; false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        Player ob = (Player) o;
        return this.user.equals(ob.getUser());
    }

    /**
     * Increases the number of the cards the Player has by one.
     */
    public void incNumCards() { numCards++; }

    /**
     * Updates the Player's information.
     */
    public void update() {
        cards = new Card[numCards];
    }

    /**
     * Sets a new number of cards for each player.
     * @param num The number of cards for each player.
     */
    public void setNumCards(int num) {
        this.numCards = num;
    }

    /**
     * Sets a new starting number of cards for each player.
     * @param num The new starting number of cards for each player.
     */
    public static void setStartNumCards(int num) {
        startNumCards = num;
    }

    /**
     * Sets a new ending number of cards for each player.
     * @param num The new ending number of cards for each player.
     */
    public static void setEndNumCards(int num) {
        endNumCards = num;
    }

    /**
     * Gets the starting number of cards for each player.
     * @return The starting number of cards for each player.
     */
    public static int getStartNumCards() {
        return startNumCards;
    }

    /**
     * Gets the ending number of cards for each player.
     * @return The ending number of cards for each player.
     */
    public static int getEndNumCards() {
        return endNumCards;
    }
}
