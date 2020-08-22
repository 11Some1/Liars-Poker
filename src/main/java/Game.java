import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;

import java.util.*;

public class Game {
    public enum GameState{NO_GAME,          // No game in progress (either hasn't started, or previous is over)
                          WAITING,          // A game has started, waiting for players to join
                          BEGIN_ROUND,      // Beginning a new round of the game
                          IN_PROGRESS }     // A round of the game is in progress

    public enum GameMode{HOLD_EM,           // Community cards
                         NORMAL       }     // No community cards
    public static int MIN_PLAYERS = 2;  // TODO: 1 when testing

    private GameState state;
    private GameMode mode;
    private ArrayList<Player> inRound;
    private int turnIdx;
    private Deck deck;
    private ArrayList<Card> poolCards;
    private ArrayList<Card> sharedCards;
    private Hand currentHand;
    private Player currentPlayer;
    private Player prevPlayer;

    /**
     * Game Constructor.
     */
    public Game() {
        state = GameState.NO_GAME;
        inRound = null;
        turnIdx = 0;
        deck = null;
        poolCards = null;
        sharedCards = null;
        currentHand = null;
        currentPlayer = null;
        prevPlayer = null;
    }

    /**
     * Displays information on the current round.
     * @return An EmbedBuilder with info on the round.
     */
    public EmbedBuilder roundInfo() {
        EmbedBuilder embd = new EmbedBuilder();
        embd.setTitle("Round info: ");
        StringBuilder sb = new StringBuilder();
        for(Player p : inRound) {
            sb.append(p.getUser().getName() + ", with " + p.getNumCards() + " card(s).");
            sb.append("\n");
        }
        embd.addField("Players still alive in the round:", sb.toString(), false);
        if(!mode.equals(GameMode.NORMAL)) {
            embd.addField("Cards in the middle:", Card.getEmotes(sharedCards.toArray(new Card[sharedCards.size()])), false);
        }
        embd.addField("Current hand to beat:", Card.getEmotes(currentHand.getHand()), false);
        sb = new StringBuilder();
        for(int i = 1; i <= inRound.size(); i++) {
            sb.append(i + ": " + inRound.get(i-1).getUser().getName());
            if(inRound.get(i-1).equals(prevPlayer)) {
                sb.append(" << *Previous Player*");
            }
            else if(inRound.get(i-1).equals(currentPlayer)) {
                sb.append(" << *Current Player*");
            }
            if(i != inRound.size()) {
                sb.append("\n");
            }
        }
        embd.addField("Order of play:", sb.toString(),false);
        embd.addField("", currentPlayer.getUser().getName() + ", it is currently your turn." +
                "\nYou may either propose a better hand,\nor challenge the previous player.", false);
        embd.setColor(0x99FF00);
        return embd;
    }

    /**
     * Gets the current game state.
     * @return state
     */
    public GameState getState() {
        return state;
    }

    /**
     * Sets the current game state.
     * @return state
     */
    public void setState(GameState g) { state = g; }

    /**
     * Adds a new player to the game.
     * @param user The Discord user in question.
     * @return true if added successfully; false if player was already in the game
     */
    public boolean addPlayer(User user) {
        if(isPlayer(user)) {
            return false;
        }
        inRound.add(new Player(user));
        return true;
    }

    /**
     * Returns whether a user is playing in the game.
     * @param user The Discord user in question.
     * @return true if player is in the game currently; false otherwise.
     */
    public boolean isPlayer(User user) {
        for(Player p : inRound) {
            if(p.getUser().equals(user)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the Player form of a Discord user.
     * @param user The Discord user in question.
     * @return The Player form of the requested Discord user, or null if requested User cannot be found.
     */
    public Player getPlayer(User user) {
        for(Player p : inRound) {
            if(p.getUser().equals(user)) {
                return p;
            }
        }
        return null;
    }

    /**
     * Removes a player from current and future rounds in the game (player got out.)
     * @param p The player in question.
     * @return true if player was removed successfully; false otherwise.
     */
    public boolean removePlayerRound(Player p) {
        for(int i = 0; i < inRound.size(); i++) {
            if(inRound.get(i).equals(p)) {
                inRound.remove(i);
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the player who is currently moving.
     * @return The player who is currently moving.
     */
    public Player currentPlayer() {
        return currentPlayer;
    }

    /**
     * Returns the current hand to beat.
     * @return The current hand to beat.
     */
    public Hand getCurrentHand() { return currentHand; }

    /**
     * Initiates a new game of Liar's Poker.
     * @param user The discord user who requested to initiate a new game.
     * @return true if game initiated successfully; false otherwise.
     */
    public boolean init(User user, GameMode mode) {
        if(state.equals(GameState.NO_GAME)) {
            inRound = new ArrayList<Player>();
            turnIdx = 0;
            deck = new Deck();
            deck.shuffle();
            poolCards = new ArrayList<Card>();
            sharedCards = new ArrayList<Card>();
            addPlayer(user);
            state = GameState.WAITING;
            this.mode = mode;
            return true;
        }
        return false;
    }

    /**
     * Begins a new game of Liar's Poker with the players who have joined.
     * @throws IllegalArgumentException if there are not at least two players ready to play.
     */ // WIP
    public void start() {
        if(state.equals(GameState.WAITING)) {
            state = GameState.BEGIN_ROUND;
            int numPlayers = inRound.size();
            if(numPlayers < MIN_PLAYERS) {
                throw new IllegalArgumentException("There must be at least two players to play.");
            }
            if(mode.equals(GameMode.HOLD_EM)) {
                Player.setEndNumCards(15 / numPlayers);
                Player.setStartNumCards((15 / numPlayers) - 2 > 0 ? (15 / numPlayers) - 2 : 1);
            }
            else if(mode.equals(GameMode.NORMAL)) {
                Player.setEndNumCards(20 / numPlayers);
                Player.setStartNumCards((20 / numPlayers) - 2 > 0 ? (20 / numPlayers) - 2 : 3);
            }
            for(Player p : inRound) {
                p.setNumCards(Player.getStartNumCards());
            }
            nextRound();
        }
    }

    /**
     * Starts a new round of Liar's Poker, dealing the appropriate amount of cards to each player and five cards in the middle.
     * Order of play and starting player is also shuffled every round.
     */
    public void nextRound() {
        if(state.equals(GameState.BEGIN_ROUND)) {
            if(mode.equals(GameMode.HOLD_EM)) {
                deck = new Deck();
                deck.shuffle();
                poolCards = new ArrayList<Card>();
                sharedCards = new ArrayList<Card>();
                for(Player p : inRound) {
                    p.update();
                    for(int i = 0; i < p.getNumCards(); i++) {
                        Card c = deck.draw();
                        p.assignCard(c, i);
                        poolCards.add(c);
                    }
                    try {
                        pmCards(p.getUser());
                    }
                    catch(IllegalStateException e) {
                        // TODO: fill in
                    }
                }
                for(int i = 0; i < 5; i++) {
                    Card c = deck.draw();
                    sharedCards.add(c);
                    poolCards.add(c);
                }
                Collections.sort(poolCards, Collections.reverseOrder());
                currentHand = new Hand(sharedCards.toArray(new Card[sharedCards.size()]));
                turnIdx = (int) (Math.random() * inRound.size());
                Collections.shuffle(inRound);
                currentPlayer = inRound.get(turnIdx);
                prevPlayer = inRound.get(Math.floorMod((turnIdx - 1), inRound.size()));
                state = GameState.IN_PROGRESS;
            }
            else if(mode.equals(GameMode.NORMAL)) {
                deck = new Deck();
                deck.shuffle();
                poolCards = new ArrayList<Card>();
                sharedCards = new ArrayList<Card>();
                for(Player p : inRound) {
                    p.update();
                    for(int i = 0; i < p.getNumCards(); i++) {
                        Card c = deck.draw();
                        p.assignCard(c, i);
                        poolCards.add(c);
                    }
                    try {
                        pmCards(p.getUser());
                    }
                    catch(IllegalStateException e) {
                        // TODO: fill in
                    }
                }
                Collections.sort(poolCards, Collections.reverseOrder());
                currentHand = new Hand("2?");
                turnIdx = (int) (Math.random() * inRound.size());
                Collections.shuffle(inRound);
                currentPlayer = inRound.get(turnIdx);
                prevPlayer = inRound.get(Math.floorMod((turnIdx - 1), inRound.size()));
                state = GameState.IN_PROGRESS;
            }
        }
    }

    /**
     * Takes a turn in the game.
     * (Note that this method does not verify whether or not the requested array of Cards is suitable to
     * be the next currentHand, ie, the method does not check if nextHand is better in rank than currentHand.
     * As it is currently, such a condition should be checked before usage of this method.)
     * @param nextHand The Hand to become the next currentHand.
     */
    public void takeTurn(Hand nextHand) {
        if(state.equals(GameState.IN_PROGRESS)) {
            currentHand = nextHand;
            prevPlayer = currentPlayer;
            turnIdx = (turnIdx + 1) % inRound.size();
            currentPlayer = inRound.get(turnIdx);
        }
    }

    /**
     * Ends the current round, or the game entirely if there is a winner at the end of the round.
     * @return An EmbedBuilder with info on the end of the round results.
     */
    public EmbedBuilder endRound() {
        EmbedBuilder embd = new EmbedBuilder();
        embd.setTitle("End of the round!");
        embd.setColor(0x99FF00);
        embd.addField("The cards in play:", Card.getEmotes(poolCards.toArray(new Card[poolCards.size()])), false);
        if(gameContains()) {
            currentPlayer.incNumCards();
            embd.addField("", "The current hand\n" + Card.getEmotes(currentHand.getHand()) + "\ncould be found in play.", false);
            embd.addField("Results:", "The current player <@" + currentPlayer.getUser().getId() + "> lost this round " +
                            "and has been given an extra card.", false);
        }
        else {
            prevPlayer.incNumCards();
            embd.addField("", "The current hand\n" + Card.getEmotes(currentHand.getHand()) + "\ncould not be found in play.", false);
            embd.addField("Results:", "The previous player <@" + prevPlayer.getUser().getId() + "> lost this round " +
                            "and has been given an extra card.", false);
        }
        for(int i = inRound.size() - 1; i >= 0; i--) {
            if(inRound.get(i).getNumCards() == Player.getEndNumCards()) {
                embd.addField("", "<@" + inRound.get(i).getUser().getId() +
                                "> now has " + Player.getEndNumCards() + " cards and has been eliminated.",
                        false);
                removePlayerRound(inRound.get(i));
            }
        }
        if(inRound.size() == 1) { //TODO: is 0 when testing
            state = GameState.NO_GAME;
            embd.setTitle("Game over!");
            embd.addField("", "<@" + inRound.get(0).getUser().getId() + "> has won the game!", false);
        }
        else {
            state = GameState.BEGIN_ROUND;
            nextRound();
        }
        return embd;
    }

    /**
     * Determines if the game's current hand can be found among all cards in play.
     * @return true if the game's current hand can be found among all cards in play; false otherwise.
     */
    public boolean gameContains() {
        ArrayList<Card> pool = new ArrayList<>();
        for(Card c : poolCards) {
            pool.add(c);
        }
        Collections.sort(pool, Collections.reverseOrder());

        for(Card c : currentHand.getHand()) {
            if(!pool.contains(c)) {
                return false;
            }
            pool.remove(c);
        }
        return true;
    }

    /**
     * Prints the given Discord user's cards in their DMs.
     * @param u The Discord user to private message.
     * @throws IllegalStateException if the Discord user does not have open DMs.
     */
    public void pmCards(User u) {
        if(!u.hasPrivateChannel()) {
            throw new IllegalStateException("This User does not have his/her DMs open!");
        }
        u.openPrivateChannel().queue((channel) ->
        {
            Arrays.sort(getPlayer(u).getCards(), Collections.reverseOrder());
            channel.sendMessage("Your cards are:\n" + Card.getEmotes(getPlayer(u).getCards())).queue();
        });
    }
}


