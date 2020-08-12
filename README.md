# Liars-Poker
A functional implementation of the card game "Liar's Poker" using a Discord bot to play it in chat.

# Impressions
![img](https://i.imgur.com/tYfGKiX.jpg)
![img](https://i.imgur.com/gIFL6Qj.jpg)
![img](https://i.imgur.com/EEPRwwZ.jpg)
![img](https://i.imgur.com/HyS6pKo.jpg)

# What is Liar's Poker?
It's Poker... but you're guessing hands among all players!

- Each player starts the game with two cards each. There are also always five cards in the middle. 
- Each person takes turns guessing what they think is the highest poker hand among all cards in play in the round. 
- Players each make guesses, with each guess being better in Poker rank than the previous, until a player decides to challenge the previous player's claim. 
- If the last hand guessed exists by combining all the cards in play in the round, the challenger (the current player) loses the round; if it doesn't exist, the previous player loses the round. 
- When a player loses a round, they get another card for all future rounds they're in, which helps... but if you get to six cards, you're out. 
- Last man standing wins!

Order of play:
1) Players are dealt their respective number of cards (two to start) and five cards to the middle.
   - The players see their cards by doing ```$gethand``` (by default), where the bot PMs their cards if the player's DMs are open.
2) The starting player and order of play is randomized.
3) When it is their turn, the player can do either of the following:
   1) Propose a new hand (```$taketurn [card 1] [card 2] ... [card 5]``` by default.) 
      - The new proposed hand must be better in Poker rank than the previous hand.
      - You may propose a hand containing one to five cards, as long as the proposed hand is better than the current.
   2) Challenge the previous player's claim.
4) Round play continues until somebody challenges.
5) When a player challenges, all the cards in play (among all players and the cards in the middle) are used to see if the last guess can be made from them.
   - If yes, the current player (the player who challenged) loses the round.
   - If no, the previous player (the player who got challenged) loses the round.
6) Whoever lost the round gets another card. If they get six cards, they are eliminated from the game.
7) Another round starts with whoever is still alive. This order of play continues until a single winner is determined.

# Commands
For the commands listed below, note that ```$``` is the default prefix. It can be changed to a different character using one of the commands.

Commands avaiable to everyone:
- ```$init```: Initiates a new game, allowing players to join.
- ```$join```: Lets you join a game that is about to begin, if able to.
- ```$start```: Begins the game with the players who have joined.
- ```$roundinfo```: Displays info on the current round.
- ```$gethand```: DMs you the cards in your hand.
- ```$taketurn [card 1] [card 2] [card 3] [card 4] [card 5]```: Proposes a new hand to beat, if it is your turn.
  - Your proposed hand must be better than the existing hand to beat.
  - You may propose a hand containing one to five cards, as long as the proposed hand is better than the current.
  - List your cards in two character strings, respectively, where the first character is the rank and the second character is the suit.
    - For example, ```Kc``` mens the king of clubs, and ```As``` means the ace of spades.
    - An example of a valid hand request is: ```$taketurn Kd 5s Tc Ah Qc```."
- ```$challenge```: Challenge the previous player's claim, if it is your turn.
- ```$commands```: Brings up the list of commands this bot offers.
- ```$gamehelp```: Brings up an explanation of how to play Liar's Poker.
- ```$rankinghelp```: Brings up an explanation of Poker hand rankings.
- ```$ping```: Checks the bot's response time to Discord.

Commands requiring the ```Administrator``` permission:
- ```$admincommands```: Brings up the list of admin commands this bot offers.
- ```$emotecurrent```: Brings up the Discord server specific addresses of the playing card emotes.
  - If you uploaded the playing card emotes directly to your server and have not updated the bot's code, these addresses will differ from those currently in the code.
    - (If you changed the names of the emotes while uploading the playing card emotes, that will screw with this command. So, don't change the names if you can help it.)
- ```$emotepre```: Brings up the currently programmed addresses of the playing card emotes. 
  - If you uploaded the playing card emotes directly to your server and have not updated the bot's code, these ddresses will differ from those currently in your server.
- ```$setprefix```: Sets the prefix for this bot's commands (```$``` by default.)
- ```$shutdown```: Disconnects the bot from the server.

# Installation
Currently, this is a *self-hosted* bot — meaning you will need to host and maintain your own instance of it.
(Perhaps later, I will maintain a public version of this bot.)

The following are the steps to take to set this bot up yourself:

1) Download the repository and save it wherever you plan on hosting the bot.
2) Go to the [Discord Developer Console](https://discord.com/developers/applications) and click "New application".
3) On the left sidebar, select "Bot".
4) Click "Add Bot"
5) Press "Click to Reveal Token" and copy the listed token.
6) Go to ```Bot.java``` and paste the token from Step 5) in place of ```[YOUR BOT TOKEN HERE]```.
7) On the left sidebar, select "OAuth2".
8) Under "Scopes", check off "bot".
9) Under "Bot permissions", select the permissions you wish to give the bot.
   - At minimum, you will need to give the bot the following permissions:
     - ```Manage Emojis```
     - ```Send Messages```
     - ```Read Message History```
     - ```Use External Emojis```
   - Alternatively, you can give the bot ```Administrator``` and be done with it, although depending on the server you might not want to or be allowed to do so.
10) After Step 9), Discord will auto generate a link to you. Go to that address. From there, you will be able to select which server you'd like to add the bot to.
    - I recommend setting up a new private server to set up the bot up on before implementing it on a different server, for reasons revealed in the next step...
      - To do so, press the green plus button on the left sidebar on the normal Discord window (```Add a server```), then click ```Create a server```, input whatever server name you want and then finally click ```Create```.
11) On your server, go to ```Server Settings``` --> ```Emoji``` --> ```Upload Emoji``` and upload all the images from the repostiory's ```img``` folder.
    - There are 31 images to upload, which is 31 of your 50 free custom Emoji slots on the server. This is why I recommend setting up a new private server for the bot before implementing it on a different server. The bot will be able to use the custom Emojis uploaded on the private server without taking up custom Emoji slots on your main server.
    - Do not change the names of the custom Emojis, or later steps won't work the intended way.
12) Turn on the bot by running the main method in ```Bot.java```. Enter ```$emotecurrent``` in one of your server's channel(s) to obtain the Discord addresses for each of the 30 custom Emojis.
    - Alteratively, you can add ```\``` in front of the custom Emoji commands to obtain the Emoji Discord address manually.
      - For example, ```\:bA:```.
      - If you change any of the uploaded Emoji names prior to obtaining their Discord address, you will have to obtain it manually this way.
13) Go to ```ImgAddress.java```. For each ```enum``` in ```ImgAddress.java```, replace the listed addresses with the appropriate addresses from step 12).
14) Setup complete! You are now free to open the bot for use.

# Limitations
- Currently, there are no wildcards.
- The bot only supports one instance of a ```Game``` at a time, meaning that you should only be actively running its commands on one server at a time. (Ie, it can only handle requests from one server currently.)
  - Having it on a private server for access to the custom Emojis is fine - Just don't use the bot there while it's running on your main server.

I hope to add these features in the future... (Literally see the next section...)

# Roadmap
Upcoming features/changes (hopefully):
- Implement wildcards.
- Add more comments to ```Bot.java```.
- Refactor a lot of the code, especailly between ```Bot.java``` and ```Game.java``` - should be better integrated/more secure.
- Support having multiple instances of a ```Game``` per bot.
  - While we're at it here, support having one bot being able to handle requests from multiple servers.
- Host a version of this bot myself.

# Credits:
- The emotes used for the cards come from the [Playing Card Emojis Discord server](https://top.gg/servers/623564336052568065?__cf_chl_jschl_tk__=e17b8191a8c7ebabd298fbfe2ce14fe85c64f98a-1597032695-0-AfY8PVb4ID-Y9xIXT83UkwxQwLK7Qm-cQvYxpppJABZASW-8wc8XIEneE2EaHtTZRmfOAfdb-vC5z75ooZhxPWsT42QyYUiOPXNtZbVoALx8FrWbLuhQf_DohpCnxnfSZemiMsXSmuQi13kqVd8YaQDnvt6l1UbNcwhwCeEIXK8auQl6k4fzsdlTMDByW0qe290lKDQ3TEMv_Nm8kBLbpcKCLk75qTZrYnEovNfa3QprCRq6IcvnMMW5XJYRIBIvNwCC4o1FEaDrDE7ldnG3ukeIW8LQG3nARtrGnwC5ypistlAJiHgTnvo9zVGMWYiq5w): run by Discord user ```@Raffael#7777```. Thanks for the images!
- This bot would not be possible without [DV8FromTheWorld](https://github.com/DV8FromTheWorld)'s [Java Discord API](https://github.com/DV8FromTheWorld/JDA), allowing me to connect my Java code to Discord.
