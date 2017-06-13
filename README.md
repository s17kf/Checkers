Checkers:
Checkers network game with classic checkers rules. It allows to play for two players in one LAN network. It is my first java project.

Build:
To build this project you need Maven software. Open project directory in command line and type: ‘mvn clean compile jfx:jar’. Executable jar file will be in:‘project_directory/target/jfx/app’.

Game:
After running the game you can see „login window”, there you can chose if you want to create new game or join to existing one. Create game means that program will create new thread named server, which will be going in background and sending messages about moves between two players. Join game means to connect to such server. After you can play checkers with your friend. To play you use mouse, click on pawn, you want to move and after that click on square you want move to. After end of game you can play again without exit application.
