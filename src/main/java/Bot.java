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
        JDA jda = JDABuilder.createDefault("[YOUR BOT TOKEN HERE]").build(); // <-- Put your bot token here
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
                        channel.sendMessage("Your cards are:\n" + Hand.toString(curGame.getPlayer(u).getCards())).queue();
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
        else if(msg[0].equals(prefix + "taketurn")) {
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
            else if(msg.length != 6) {
                embd.setTitle("Failed to take a turn...");
                embd.setDescription("Your request for a hand does not contain exactly 5 cards.");
                embd.setColor(0xf45642);
            }
            else {
                try {
                    String str = (msg[1] + " " + msg[2] + " " + msg[3] + " " + msg[4] + " " + msg[5]);
                    if(Hand.evaluate(Hand.fromString(str)) >= Hand.evaluate(curGame.getCurrentHand())) {
                        embd.setTitle("Failed to take a turn...");
                        embd.setDescription("Your request for a hand\n" + Hand.toString(Hand.fromString(str)) +
                                "\nis not better than the current hand.");
                        embd.setColor(0xf45642);
                    }
                    else {
                        curGame.takeTurn(Hand.fromString(str));
                        embd.setTitle(event.getMember().getEffectiveName() + " has taken his/her turn!");
                        embd.setDescription("It is now <@" + curGame.currentPlayer().getUser().getId() + ">'s turn.");
                        embd.appendDescription("\nYou may either propose a better hand,\nor challenge the previous player.");
                        embd.addField("Current hand to beat:", Hand.toString(curGame.getCurrentHand()), false);
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
            embd.addField("`" + prefix + "taketurn [card 1] [card 2] [card 3] [card 4] [card 5]`",
                    "Propose a new hand to beat, if it is your turn." +
                            "\nYour proposed hand must be better than the existing hand to beat." +
                            "\nList your cards in two character strings, respectively," +
                            "\nwhere the first character is the rank and the second character is the suit." +
                            "\nFor example, \"Kc\" means the king of clubs, and \"As\" means the ace of spades." +
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
                    "get to six cards, you're out. ", false);
            embd.addField("Order of play:", "1) Players are dealt their respective number of cards (two to start) " +
                            "and five cards to the middle.\n2) The starting player and order of play is randomized. " +
                            "\n3) When guessing, the player either proposes a new hand  with " +
                    "`" + prefix + "taketurn [card 1] [card 2] [card 3] [card 4] [card 5]`" + "or `" + prefix + "challenge`s" +
                    " the previous player's claim. The new proposed hand must be better in Poker rank than the previous hand " +
                    "(see `" + prefix + "rankinghelp` for information about Poker hand rankings.)\n4) Round play continues until somebody " +
                    "challenges.\n5) When a player challenges, all the cards in play (among all players and the cards in the middle) " +
                    "are used to see if the last guess can be made from them. If yes, the current player (who challenged) loses the round; " +
                    "else, the previous player (who got challenged) loses the round. \n6) Whoever lost the round gets another card. " +
                    "If they get six cards, they are eliminated from the game.\n7) Another round starts with whoever is still alive. " +
                    "This continues until a single winner is determined.", false);
            embd.addField("Other fine details...", "-Currently, aces are high only.\n-Currently, straights do not wrap around." +
                            "\n-Currently, your guess must be exactly 5 cards.\n-Currently, you must specify a suit for each card." +
                            "\n-Currently, there are no wildcards.\n(Perhaps in the future, these will be features added.)",
                    false);
            embd.setColor(0x99FF00);
            event.getChannel().sendMessage(embd.build()).queue();
        }
        else if(msg[0].equals(prefix + "rankinghelp")) {
            EmbedBuilder embd = new EmbedBuilder();
            embd.setFooter("Requested by " + event.getMember().getEffectiveName(), event.getMember().getUser().getAvatarUrl());
            embd.setTitle("Poker Hand Rankings:");
            final Card[] STRAIGHT_FLUSH = {Card.fromString("5h"), Card.fromString("6h"), Card.fromString("7h"),
                    Card.fromString("8h"), Card.fromString("9h")},
            FOUR_KIND = {Card.fromString("3c"), Card.fromString("3d"), Card.fromString("3h"), Card.fromString("3s"), Card.fromString("4s")},
            FULL_HOUSE = {Card.fromString("Jd"), Card.fromString("Jh"), Card.fromString("Js"), Card.fromString("Ac"), Card.fromString("Ah")},
            FLUSH = {Card.fromString("5s"), Card.fromString("7s"), Card.fromString("Ts"), Card.fromString("Qs"), Card.fromString("Ks")},
            STRAIGHT = {Card.fromString("6h"), Card.fromString("7d"), Card.fromString("8c"), Card.fromString("9c"), Card.fromString("Ts")},
            THREE_KIND = {Card.fromString("5c"), Card.fromString("5d"), Card.fromString("5s"), Card.fromString("8s"), Card.fromString("Jd")},
            TWO_PAIR = {Card.fromString("4c"), Card.fromString("4h"), Card.fromString("9d"), Card.fromString("9s"), Card.fromString("As")},
            ONE_PAIR = {Card.fromString("Td"), Card.fromString("Th"), Card.fromString("Jh"), Card.fromString("Kc"), Card.fromString("Ac")},
            HIGH_CARD = {Card.fromString("4h"), Card.fromString("5s"), Card.fromString("8s"), Card.fromString("Td"), Card.fromString("Kd")};
            embd.setDescription("Suit rankings:\n" + FOUR_KIND[0].suitImg() + " < " + FOUR_KIND[1].suitImg() +
                    " < " + FOUR_KIND[2].suitImg() + " < " + FOUR_KIND[3].suitImg() + "\n\nHands from highest to lowest rank:");
            embd.addField("Straight flush", Hand.toString(STRAIGHT_FLUSH) +
                    "\nFive cards of sequential rank of the same suit.", false);
            embd.addField("Four of a kind", Hand.toString(FOUR_KIND) +
                    "\nFour cards of one rank, one card of another rank.", false);
            embd.addField("Full house", Hand.toString(FULL_HOUSE) +
                    "\nThree cards of one rank, and two cards of another rank.", false);
            embd.addField("Flush", Hand.toString(FLUSH) +
                    "\nFive cards of the same suit, not all of sequential rank.", false);
            embd.addField("Straight", Hand.toString(STRAIGHT) +
                    "\nFive cards of sequential rank, not all of the same suit.", false);
            embd.addField("Three of a kind", Hand.toString(THREE_KIND) +
                    "\nThree cards of one rank and two cards of two other ranks.", false);
            embd.addField("Two pair", Hand.toString(TWO_PAIR) +
                    "\nTwo cards of one rank, two cards of another rank, one card of a third rank.", false);
            embd.addField("One pair", Hand.toString(ONE_PAIR) +
                    "\nTwo cards of one rank, three cards of three other ranks.", false);
            embd.addField("High card", Hand.toString(HIGH_CARD) +
                    "\nCards are all of distinct ranks, are not all sequential in rank, and not all of the same suit.", false);
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
            embd.setDescription("**Currently coded card ranks:**");
            Deck tp = new Deck();
            for(int i = 1; i <= 25; i++) {
                Card c = tp.draw();
                embd.appendDescription("\n" + c.rankImg() + "= `" + c.rankImg() + "`");
            }
            Card c = tp.draw();
            embd.appendDescription("\n" + c.rankImg() + "= `" + c.rankImg() + "`" + "\n\n**Currently coded card suits:**");
            embd.appendDescription("\n" + c.suitImg() + "= `" + c.suitImg() + "`");
            c = tp.draw();
            embd.appendDescription("\n" + c.suitImg() + "= `" + c.suitImg() + "`");
            c = tp.draw();
            embd.appendDescription("\n" + c.suitImg() + "= `" + c.suitImg() + "`");
            c = Card.fromString("Ac");
            embd.appendDescription("\n" + c.suitImg() + "= `" + c.suitImg() + "`");
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
            embd.addField("`" + prefix + "setprefix`", "Sets the prefix for this bot." +
                    "\nThe current prefix is: `" + prefix + "`", false);
            embd.addField("`" + prefix + "shutdown`", "Disconnects the bot from the server.", false);
            embd.setColor(0x99FF00);
            event.getChannel().sendMessage(embd.build()).queue();
        }
    }
}