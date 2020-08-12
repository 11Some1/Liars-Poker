import java.util.*;

import javax.security.auth.login.LoginException;
import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Bot extends ListenerAdapter {

    private static Game curGame;
    private static char prefix = '$'; // Default

    public static void main(String[] args) throws Exception {
        JDA jda = JDABuilder.createDefault("NzQwNjkyMjg4MTkxMjAxMzIy.Xystcg.I1MWCtYOJgbiECHOQimfMDNmuMc").build(); // <-- Put your bot token here
        jda.getPresence().setStatus(OnlineStatus.IDLE);
        jda.getPresence().setActivity(Activity.watching("Liar's Poker!"));
        jda.addEventListener(new Bot());
        curGame = new Game();
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        String[] msg = event.getMessage().getContentRaw().split(" ");
        if(event.getAuthor().isBot()) {
            return;
        }
        if(msg[0].equals(prefix + "init")) {
            EmbedBuilder embd = new EmbedBuilder();
            embd.setFooter("Requested by " + event.getMember().getEffectiveName(), event.getMember().getUser().getAvatarUrl());
            if(curGame.init(event.getAuthor())) {
                embd.setTitle("New game initiated!");
                embd.setDescription("A new game has been started by " + event.getMember().getEffectiveName() + "!"
                    + "\nMessage `" + prefix + "join` to join the game.");
                embd.setColor(0x99FF00);
            }
            else {
                embd.setTitle("Failed to initiate new game...");
                embd.setDescription("There is already a game in progress; you can't start a new game. :(");
                if(curGame.getState().equals(Game.GameState.WAITING)) {
                    embd.appendDescription("\nHowever, the game hasn't started yet, so you can still join."
                            + "\nMessage `" + prefix + "join` to join the game.");
                }
                embd.setColor(0xf45642);
            }
            event.getChannel().sendMessage(embd.build()).queue();
        }
        else if(msg[0].equals(prefix + "join")) {
            EmbedBuilder embd = new EmbedBuilder();
            embd.setFooter("Requested by " + event.getMember().getEffectiveName(), event.getMember().getUser().getAvatarUrl());
            if(curGame.getState().equals(Game.GameState.NO_GAME)) {
                embd.setTitle("Failed to join game...");
                embd.setDescription("No game has been initiated yet for you to join.\nMessage `" + prefix + "init` to start a new game.");
                embd.setColor(0xf45642);
            }
            else if(!curGame.getState().equals(Game.GameState.WAITING)) {
                embd.setTitle("Failed to join game...");
                embd.setDescription("This game is already in progress.\nYou're not allowed to join right now.");
                embd.setColor(0xf45642);
            }
            else {
                if(curGame.addPlayer(event.getMember().getUser())) {
                    embd.setTitle("Joined game successfully!");
                    embd.setDescription(event.getMember().getEffectiveName() + " has joined the game!"
                            + "\nMessage `" + prefix + "join` to join the game.");
                    embd.setColor(0x99FF00);
                }
                else {
                    embd.setTitle("Failed to join game...");
                    embd.setDescription("You've already joined the game...");
                    embd.setColor(0xf45642);
                }
            }
            event.getChannel().sendMessage(embd.build()).queue();
        }
        else if(msg[0].equals(prefix + "start")) {
            EmbedBuilder embd = new EmbedBuilder();
            boolean disp = false;
            embd.setFooter("Requested by " + event.getMember().getEffectiveName(), event.getMember().getUser().getAvatarUrl());
            if(curGame.getState().equals(Game.GameState.NO_GAME)) {
                embd.setTitle("Failed to start game...");
                embd.setDescription("No game has been initiated yet for you to start.\nMessage `" + prefix + "init` to start a new game.");
                embd.setColor(0xf45642);
            }
            else if(!curGame.getState().equals(Game.GameState.WAITING)) {
                embd.setTitle("Failed to start game...");
                embd.setDescription("The game has already been started.");
                embd.setColor(0xf45642);
            }
            else if(!curGame.isPlayer(event.getMember().getUser())) {
                embd.setTitle("Failed to start game...");
                embd.setDescription("You are not a part of that game yet.\nPlease message `" + prefix + "join` to join the current game.");
                embd.setColor(0xf45642);
            }
            else {
                embd.setTitle("Started the game!");
                embd.setDescription("The game has been started!");
                embd.setColor(0x99FF00);
                curGame.start();
                disp = true;
            }
            event.getChannel().sendMessage(embd.build()).queue();
            if(disp) {
                embd = curGame.roundInfo();
                embd.setTitle("New round!");
                embd.addField("", "Run `" + prefix + "gethand` to see your cards for this round.", false);
                event.getChannel().sendMessage(embd.build()).queue();
            }
        }
        else if(msg[0].equals(prefix + "roundinfo")) {
            EmbedBuilder embd = new EmbedBuilder();
            if(curGame.getState().equals(Game.GameState.NO_GAME)) {
                embd.setTitle("Failed to get round info...");
                embd.setDescription("No game has been initiated yet, so you can't get round info."
                        + "\nMessage `" + prefix + "init` to start a new game.");
                embd.setColor(0xf45642);
            }
            else if(curGame.getState().equals(Game.GameState.WAITING)) {
                embd.setTitle("Failed to get round info...");
                embd.setDescription("A round is not in progress currently.");
                embd.setColor(0xf45642);
            }
            else {
                embd = curGame.roundInfo();
            }
            embd.setFooter("Requested by " + event.getMember().getEffectiveName(), event.getMember().getUser().getAvatarUrl());
            event.getChannel().sendMessage(embd.build()).queue();
        }
        else if(msg[0].equals(prefix + "gethand")) {
            EmbedBuilder embd = new EmbedBuilder();
            embd.setFooter("Requested by " + event.getMember().getEffectiveName(), event.getMember().getUser().getAvatarUrl());
            if(curGame.getState().equals(Game.GameState.NO_GAME)) {
                embd.setTitle("Failed to get your cards...");
                embd.setDescription("No game has been initiated yet, so you can't get round info (including your cards."
                        + "\nMessage `" + prefix + "init` to start a new game.");
                embd.setColor(0xf45642);
            }
            else if(curGame.getState().equals(Game.GameState.WAITING)) {
                embd.setTitle("Failed to get your cards...");
                embd.setDescription("A round is not in progress currently, so you can't get round info (including your cards.)");
                embd.setColor(0xf45642);
            }
            else if(!curGame.isPlayer(event.getMember().getUser())) {
                embd.setTitle("Failed to get your cards...");
                embd.setDescription("You are not in the current round.");
                embd.setColor(0xf45642);
            }
            else {
                User u = event.getMember().getUser();
                if(true) { //TODO: if(u.hasPrivateChannel())
                    u.openPrivateChannel().queue((channel) ->
                    {
                        channel.sendMessage("Your cards are:\n" + Card.getEmotes(curGame.getPlayer(u).getCards())).queue();
                    });
                    embd.setTitle("Cards sent!");
                    embd.setDescription("Check your DMs to see your cards.");
                    embd.setColor(0x99FF00);
                }
                else {
                    embd.setTitle("Failed to send cards...");
                    embd.setDescription("<@" + u.getId() + ">, your DMs are closed!\nPlease make sure your DMs are enabled.");
                    embd.setColor(0xf45642);
                }
            }
            event.getChannel().sendMessage(embd.build()).queue();
        }
        else if(msg[0].equals(prefix + "taketurn") && msg.length >= 2) {
            EmbedBuilder embd = new EmbedBuilder();
            embd.setFooter("Requested by " + event.getMember().getEffectiveName(), event.getMember().getUser().getAvatarUrl());
            if(curGame.getState().equals(Game.GameState.NO_GAME)) {
                embd.setTitle("Failed to take a turn...");
                embd.setDescription("No game has been initiated yet, so you can't take a turn in a round.."
                        + "\nMessage `" + prefix + "init` to start a new game.");
                embd.setColor(0xf45642);
            }
            else if(curGame.getState().equals(Game.GameState.WAITING)) {
                embd.setTitle("Failed to take a turn...");
                embd.setDescription("A round is not in progress currently, so you can't take a turn in a round.");
                embd.setColor(0xf45642);
            }
            else if(!curGame.isPlayer(event.getMember().getUser())) {
                embd.setTitle("Failed to take a turn...");
                embd.setDescription("You are not in the current round.");
                embd.setColor(0xf45642);
            }
            else if(!curGame.currentPlayer().getUser().equals(event.getMember().getUser())) {
                embd.setTitle("Failed to take a turn...");
                embd.setDescription("It is not your turn.");
                embd.setColor(0xf45642);
            }
            else if(msg.length > 6) {
                embd.setTitle("Failed to take a turn...");
                embd.setDescription("Your request for a hand contains more than 5 cards.");
                embd.setColor(0xf45642);
            }
            else {
                try {
                    StringBuilder sb = new StringBuilder();
                    for(int i = 1; i < msg.length; i++) {
                        sb.append(msg[i]);
                        if(i != msg.length - 1) sb.append(" ");
                    }
                    Hand h = new Hand(sb.toString());
                    if(h.compareTo(curGame.getCurrentHand()) <= 0) {
                        embd.setTitle("Failed to take a turn...");
                        embd.setDescription("Your request for a hand\n" + Card.getEmotes(h.getHand()) +
                                "\nis not better than the current hand.");
                        embd.setColor(0xf45642);
                    }
                    else {
                        curGame.takeTurn(h);
                        embd.setTitle(event.getMember().getEffectiveName() + " has taken his/her turn!");
                        embd.setDescription("It is now <@" + curGame.currentPlayer().getUser().getId() + ">'s turn.");
                        embd.appendDescription("\nYou may either propose a better hand,\nor challenge the previous player.");
                        embd.addField("Current hand to beat:", Card.getEmotes(curGame.getCurrentHand().getHand()), false);
                        embd.setColor(0x99FF00);
                    }
                }
                catch(IllegalArgumentException e) {
                    embd.setTitle("Failed to take a turn...");
                    embd.setDescription("Your request for a hand is invalid." +
                            "\nAn example of a valid hand request is:\n`$taketurn Kd 5s Tc Ah Qc`.");
                    embd.setColor(0xf45642);
                }
            }
            event.getChannel().sendMessage(embd.build()).queue();
        }
        else if(msg[0].equals(prefix + "challenge")) {
            EmbedBuilder embd = new EmbedBuilder();
            embd.setFooter("Requested by " + event.getMember().getEffectiveName(), event.getMember().getUser().getAvatarUrl());
            boolean disp = false;
            if(curGame.getState().equals(Game.GameState.NO_GAME)) {
                embd.setTitle("Failed to challenge the previous player...");
                embd.setDescription("No game has been initiated yet, so you can't challenge in the round..."
                        + "\nMessage `" + prefix + "init` to start a new game.");
                embd.setColor(0xf45642);
            }
            else if(curGame.getState().equals(Game.GameState.WAITING)) {
                embd.setTitle("Failed to challenge the previous player...");
                embd.setDescription("A round is not in progress currently, so you can't challenge in the round.");
                embd.setColor(0xf45642);
            }
            else if(!curGame.isPlayer(event.getMember().getUser())) {
                embd.setTitle("Failed to challenge the previous player...");
                embd.setDescription("You are not in the current round.");
                embd.setColor(0xf45642);
            }
            else if(!curGame.currentPlayer().getUser().equals(event.getMember().getUser())) {
                embd.setTitle("Failed to challenge the previous player...");
                embd.setDescription("It is not your turn.");
                embd.setColor(0xf45642);
            }
            else {
                embd = curGame.endRound();
                if(!curGame.getState().equals(Game.GameState.NO_GAME))
                disp = true;
            }
            event.getChannel().sendMessage(embd.build()).queue();
            if(disp) {
                embd = curGame.roundInfo();
                embd.setTitle("New round!");
                embd.addField("", "Run `" + prefix + "gethand` to see your cards for this round.", false);
                event.getChannel().sendMessage(embd.build()).queue();
            }
        }
        else if(msg[0].equals(prefix + "commands")) {
            EmbedBuilder embd = new EmbedBuilder();
            embd.setFooter("Requested by " + event.getMember().getEffectiveName(), event.getMember().getUser().getAvatarUrl());
            embd.setTitle("List of commands:");
            embd.addField("`" + prefix + "init`", "Initiates a new game, allowing players to join.", false);
            embd.addField("`" + prefix + "join`", "Lets you join a game that is about to begin, if able to.", false);
            embd.addField("`" + prefix + "start`", "Begins the game with the players who have joined." , false);
            embd.addField("`" + prefix + "roundinfo`", "Displays info on the current round.", false);
            embd.addField("`" + prefix + "gethand`", "DMs you the cards in your hand.", false);
            embd.addField("`" + prefix + "taketurn [card 1] [card 2] ... [card 5]`",
                    "Propose a new hand to beat, if it is your turn." +
                            "\nYour proposed hand must be better in Poker rank than the existing hand to beat." +
                            "\nYou may propose a hand containing one to five cards, as long as the proposed hand is better than the current." +
                            "\nList your cards in two character strings, respectively, " +
                            "where the first character is the rank and the second character is the suit." +
                            "\nFor example, \"Kc\" means the king of clubs, and \"A?\" means an ace without the suit specified." +
                            "\nAn example of a valid hand request is:\n`" + prefix + "taketurn Kd 5s Tc Ah Qc`.", false);
            embd.addField("`" + prefix + "challenge`", "Challenge the previous player's claim, if it is your turn.", false);
            embd.addField("`" + prefix + "commands`", "Brings up the list of commands this bot offers.", false);
            embd.addField("`" + prefix + "gamehelp`", "Brings up an explanation of how to play Liar's Poker.", false);
            embd.addField("`" + prefix + "rankinghelp`", "Brings up an explanation of Poker hand rankings.", false);
            embd.addField("`" + prefix + "ping`", "Checks the bot's response time to Discord.", false);
            embd.setColor(0x99FF00);
            event.getChannel().sendMessage(embd.build()).queue();
        }
        else if(msg[0].equals(prefix + "gamehelp")) {
            EmbedBuilder embd = new EmbedBuilder();
            embd.setFooter("Requested by " + event.getMember().getEffectiveName(), event.getMember().getUser().getAvatarUrl());
            embd.setTitle("Welcome to Liar's Poker!");
            embd.setDescription("It's Poker... but you're guessing hands among all players!");
            embd.addField("Basic description:", "Each person starts the game with two cards each. There are also always " +
                    "five cards in the middle.\nEach person takes turns guessing what they think is the highest poker hand among all " +
                    "cards in play in the round.\nPlayers each make guesses, with each guess being better in Poker rank than the " +
                    "previous, until a player decides to challenge the previous player's claim.\nIf the last hand " +
                    "guessed exists by combining all the cards in play in the round, the challenger (the current " +
                    "player) loses the round; if it doesn't exist, the previous player loses the round.\nWhen a " +
                    "player loses a round, they get another card for all future rounds they're in, which helps...\n...but if you " +
                    "get to six cards, you're out.\nLast man standing wins!", false);
            embd.addField("Order of play:", "1) Players are dealt their respective number of cards (two to start) " +
                            "and five cards to the middle.\n2) The starting player and order of play is randomized. " +
                            "\n3) When it is their turn, the player either proposes a new hand better in Poker rank than the previous hand with" +
                    "\n`" + prefix + "taketurn [card 1] [card 2] ... [card 5]`" + "\nor `" + prefix + "challenge`s" +
                    " the previous player's claim. (See `" + prefix + "rankinghelp` for information about Poker hand rankings.)" +
                    "\n4) Round play continues until somebody challenges." +
                    "\n5) When a player challenges, all the cards in play (among all players and the cards in the middle) " +
                    "are used to see if the last guess can be made from them. If yes, the current player (the player who challenged) " +
                    "loses the round; If no, the previous player (the player who got challenged) loses the round. \n6) Whoever lost " +
                    "the round gets another card. If they get six cards, they are eliminated from the game.\n7) Another round starts " +
                    "with whoever is still alive. This order of play continues until a single winner is determined.", false);
            embd.addField("Current limitations:", " -There are no wildcards.\n(Perhaps in the future, these will be features added.)",
                    false);
            embd.setColor(0x99FF00);
            event.getChannel().sendMessage(embd.build()).queue();
        }
        else if(msg[0].equals(prefix + "rankinghelp")) {
            EmbedBuilder embd = new EmbedBuilder();
            embd.setFooter("Requested by " + event.getMember().getEffectiveName(), event.getMember().getUser().getAvatarUrl());
            embd.setTitle("Poker Hand Rankings:");

            final Hand STRAIGHT_FLUSH = new Hand("5h 6h 7h 8h 9h"), FOUR_KIND = new Hand("3c 3d 3h 3s 4s"),
            FULL_HOUSE = new Hand("Jd Jh Js Ac Ah"), FLUSH = new Hand("5s 7s Ts Qs Ks"),
            STRAIGHT = new Hand("6h 7d 8c 9c Ts"), THREE_KIND = new Hand("5c 5d 5s 8s Jd"),
            TWO_PAIR = new Hand("4c 4h 9d 9s As"), ONE_PAIR = new Hand("Td Th Jh Kc Ac"),
            HIGH_CARD = new Hand("4h 5s 8s Td Kd"), EX_1 = new Hand("3? 3d 3h 5s 7s"), EX_2 = new Hand("3d 3h 3s 5s 7s");

            embd.addField("Straight flush", Card.getEmotes(STRAIGHT_FLUSH.getHand()) +
                    "\nFive cards of sequential rank of the same suit.", false);
            embd.addField("Four of a kind", Card.getEmotes(FOUR_KIND.getHand()) +
                    "\nFour cards of one rank, one card of another rank (the kicker.)", false);
            embd.addField("Full house", Card.getEmotes(FULL_HOUSE.getHand()) +
                    "\nThree cards of one rank, and two cards of another rank.", false);
            embd.addField("Flush", Card.getEmotes(FLUSH.getHand()) +
                    "\nFive cards of the same suit, not all of sequential rank.", false);
            embd.addField("Straight", Card.getEmotes(STRAIGHT.getHand()) +
                    "\nFive cards of sequential rank, not all of the same suit.", false);
            embd.addField("Three of a kind", Card.getEmotes(THREE_KIND.getHand()) +
                    "\nThree cards of one rank and two cards of two other ranks (two kickers.)", false);
            embd.addField("Two pair", Card.getEmotes(TWO_PAIR.getHand()) +
                    "\nTwo cards of one rank, two cards of another rank, one card of a third rank (the kicker.)", false);
            embd.addField("One pair", Card.getEmotes(ONE_PAIR.getHand()) +
                    "\nTwo cards of one rank, three cards of three other ranks (three kickers.)", false);
            embd.addField("High card", Card.getEmotes(HIGH_CARD.getHand()) +
                    "\nCards are all of distinct ranks, are not all sequential in rank, and not all of the same suit.", false);
            embd.addField("-------------------------------------------------------", "", false);
            embd.addField("Suit rankings:", ImgAddress.sN.getAddress() + " < " + ImgAddress.sC.getAddress() + " < " +
                    ImgAddress.sD.getAddress() + " < " + ImgAddress.sH.getAddress() + " < " + ImgAddress.sS.getAddress(), false);
            embd.addField("If Hands have the same rank:", "Cards in the hands are compared individually from left to right." +
                    "\nCards are compared on rank first, then suit. " +
                    "\nIf one card is better than the other, the hand that it belongs to is the better one." +
                    "\nIf, when comparing at a given index, one hand has a card but the other does not have one at that index," +
                    " the hand that actually has the card is the better one.", false);
            embd.addField("Hands without suit specified:",
                    "A card need not necessarily have its suit specified." +
                    "\nSimply enter `?` in place of the suit; ie, `6?`" +
                    "\nCards w/out specified suits counts towards non-flush ranks (which require a specified suit); for example, in" +
                    "\n" + Card.getEmotes(EX_1.getHand()) + "\n`3?` counts towards completing a three of a kind." +
                    "\nNon specified suits are lower than those specified;" +
                    "\nAs such, the above example loses to \n" + Card.getEmotes(EX_2.getHand()) + "." +
                    "\nWhen checking if cards are present, one w/out a specified suit counts for a card w/ the" +
                    " same rank and a specified suit.\nFor example, `4?` counts for `4s`.", false);
            embd.setColor(0x99FF00);
            event.getChannel().sendMessage(embd.build()).queue();
        }
        else if(msg[0].equals(prefix + "ping")) {
            MessageChannel channel = event.getChannel();
            long time = System.currentTimeMillis();
            channel.sendMessage("PONG!").queue(response /* => Message */ -> {
                response.editMessageFormat("PONG! %d ms", System.currentTimeMillis() - time).queue();
            });
        }
        else if(msg[0].equals(prefix + "emotecurrent") && event.getMember().hasPermission(Permission.ADMINISTRATOR)) {
            EmbedBuilder embd = new EmbedBuilder();
            embd.setFooter("Requested by " + event.getMember().getEffectiveName(), event.getMember().getUser().getAvatarUrl());
            List<Emote> list = event.getJDA().getEmotes();
            embd.setDescription("**Emote Addresses:**");
            for(Emote e : list) {
                if(e.getName().length() == 2) {
                    switch(e.getName().charAt(0)) {
                        case 'b':
                        case 'r':
                        case 's':
                            switch(e.getName().charAt(1)) {
                                case 'A':
                                case 'C':
                                case 'D':
                                case 'H':
                                case 'S':
                                case '2':
                                case '3':
                                case '4':
                                case '5':
                                case '6':
                                case '7':
                                case '8':
                                case '9':
                                case 'T':
                                case 'J':
                                case 'Q':
                                case 'K':
                                case 'N':
                                    embd.appendDescription("\n<:" + e.getName() + ":" + e.getId() + ">");
                                    embd.appendDescription("= `<:" + e.getName() + ":" + e.getId() + ">`");
                                    break;
                            }
                            break;

                    }
                }
            }
            embd.setColor(0x99FF00);
            event.getChannel().sendMessage(embd.build()).queue();
        }
        else if(msg[0].equals(prefix + "emotepre") && event.getMember().hasPermission(Permission.ADMINISTRATOR)) {
            EmbedBuilder embd = new EmbedBuilder();
            embd.setFooter("Requested by " + event.getMember().getEffectiveName(), event.getMember().getUser().getAvatarUrl());
            embd.setDescription("**Currently coded card ranks and suits:**");
            for(ImgAddress address : ImgAddress.values()) {
                embd.appendDescription("\n" + address.getAddress() + "= `" + address.getAddress() + "`");
            }
            embd.setColor(0x99FF00);
            event.getChannel().sendMessage(embd.build()).queue();
        }
        else if(msg[0].equals(prefix + "setprefix") && msg.length == 2 && event.getMember().hasPermission(Permission.ADMINISTRATOR)) {
            EmbedBuilder embd = new EmbedBuilder();
            embd.setFooter("Requested by " + event.getMember().getEffectiveName(), event.getMember().getUser().getAvatarUrl());
            if(msg[1].length() == 1) {
                prefix = msg[1].charAt(0);
                embd.setTitle("Prefix set!");
                embd.setDescription("The prefix is now `" + prefix + "`.");
                embd.setColor(0x99FF00);
            }
            else {
                embd.setTitle("Failed to set prefix...");
                embd.setDescription("The prefix has to be one character only.");
                embd.setColor(0xf45642);
            }
            event.getChannel().sendMessage(embd.build()).queue();
        }
        else if(msg[0].equals(prefix + "shutdown") && event.getMember().hasPermission(Permission.ADMINISTRATOR)) {
            event.getChannel().sendMessage("Goodbye.").queue();
            System.exit(0);
        }
        else if(msg[0].equals(prefix + "admincommands") && event.getMember().hasPermission(Permission.ADMINISTRATOR)) {
            EmbedBuilder embd = new EmbedBuilder();
            embd.setFooter("Requested by " + event.getMember().getEffectiveName(), event.getMember().getUser().getAvatarUrl());
            embd.setTitle("List of admin commands:");
            embd.addField("`" + prefix + "admincommands`", "Brings up the list of admin commands this bot offers." +
                    "\nThese commands require administrator permission.", false);
            embd.addField("`" + prefix + "emotecurrent`", "Brings up the Discord server specific " +
                    "addresses of the playing card emotes.\nIf you uploaded the playing card emotes directly to your server" +
                    " and have not updated the bot's code, these addresses will differ from those currently in the code." +
                    "\n(If you changed the names of the emotes while uploading the playing card emotes," +
                    " that will screw with this command. So, don't change the names if you can help it.)", false);
            embd.addField("`" + prefix + "emotepre`", "Brings up the currently programmed addresses of the playing" +
                    "card emotes.\nIf you uploaded the playing card emotes directly to your server and have not updated the bot's code," +
                    " these addresses will differ from those currently in your server.", false);
            embd.addField("`" + prefix + "setprefix`", "Sets the prefix for this bot's commands (`$` by default.)" +
                    "\nThe current prefix is: `" + prefix + "`", false);
            embd.addField("`" + prefix + "shutdown`", "Disconnects the bot from the server.", false);
            embd.setColor(0x99FF00);
            event.getChannel().sendMessage(embd.build()).queue();
        }
    }
}