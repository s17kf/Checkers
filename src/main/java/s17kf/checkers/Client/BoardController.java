package s17kf.checkers.Client;

import s17kf.checkers.GameLogic.Chessboard;
import s17kf.checkers.GameLogic.Coordinates;
import s17kf.checkers.GameLogic.MoveParameters;
import s17kf.checkers.GameLogic.Pawn;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.input.MouseEvent;
import javafx.fxml.FXML;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;


/**
 * Created by Stefan on 2017-05-19.
 */
public class BoardController{

    @FXML
    private GridPane boardVisual;

    @FXML
    private BorderPane rootPane;

    @FXML
    Pane square0, square1, square2, square3, square4, square5, square6, square7, square8, square9, square10, square11, square12, square13, square14, square15, square16, square17, square18, square19, square20;
    @FXML
    Pane square21, square22, square23, square24, square25, square26, square27, square28, square29, square30, square31, square32, square33, square34, square35, square36, square37, square38, square39, square40;
    @FXML
    Pane square41, square42, square43, square44, square45, square46, square47, square48, square49, square50, square51, square52, square53, square54, square55, square56, square57, square58, square59, square60, square61, square62, square63;

    @FXML
    Label whoMoveLabel;

    @FXML
    VBox leftIndexLabels, rightIndexLabels;
    @FXML
    HBox upIndexLabels, downIndexLabels;

    @FXML
    HBox player1HitPawns, player2HitPawns;

    @FXML
    TextArea logTextArea;

    private Map<Integer, Pane> squaresMap;
    private Vector<Integer> possibleMoves;
    private Integer lastClicked;

    private Boolean isPlayer1White;
    private Chessboard board;
    private ConnectionController connectionController;
    private int activePlayer;
    Thread incomingMessagesReader;
    Boolean wantToPlayAgain;
    private Boolean isGameEnded;

    public BoardController(){

        squaresMap = new HashMap<>();
        possibleMoves = new Vector<>();
        isGameEnded = false;

    }

    public void initBoard(String ipAddress, String portNumber) throws Exception{

        rootPane.setStyle("-fx-background-color: lightslategrey");
        System.out.println("Init Board");
        connectionController = new ConnectionController(ipAddress, portNumber, this);
        System.out.println("color received");
        String color = new String();

        color = connectionController.readMessage();



        initSquaresMap();

        isPlayer1White = color.equals("1");

        createChessboard(Integer.parseInt(color));

    }



    void createChessboard(int activePlayer) throws Exception{
        createChessboard(activePlayer == 1);
    }

    void createChessboard(Boolean isPlayer1White) throws Exception{
        System.out.println("creating board");
        this.isPlayer1White = isPlayer1White;
        activePlayer = 1;
        board=new Chessboard(isPlayer1White);
        board.setActivePlayer(isPlayer1White ? 1 : 2);
        initIndexLabels(isPlayer1White);

        int colorInfluence = isPlayer1White ? 0 : 12;

        resetChessboard();

        for(int i=0 + colorInfluence;i<12 + colorInfluence;i++){
            putPawnOnSquare(i, board.getPawn(i).getPosition().toInt(),Color.WHITE);
        }
        for(int i=12-colorInfluence;i<24-colorInfluence;i++){
            putPawnOnSquare(i, board.getPawn(i).getPosition().toInt(),Color.BLACK);
        }
        updateWhoMoveLabel();

        resetHitPawns();

        incomingMessagesReader = new Thread(connectionController);
        incomingMessagesReader.start();

    }

    void resetChessboard(){
        for( int i = 0; i < 63; i++){
            if(squaresMap.get(i).getChildren().size() > 1)
                squaresMap.get(i).getChildren().remove(1,squaresMap.get(i).getChildren().size());
        }
    }

    void resetHitPawns(){
        if(player1HitPawns.getChildren().size() > 0)
            player1HitPawns.getChildren().remove(0,player1HitPawns.getChildren().size());
        if(player2HitPawns.getChildren().size() > 0)
            player2HitPawns.getChildren().remove(0,player2HitPawns.getChildren().size());
    }

    public Chessboard getBoard() {
        return board;
    }

    public Boolean getWantToPlayAgain() {
        return wantToPlayAgain;
    }

    public void setWantToPlayAgain(Boolean wantToPlayAgain) {
        this.wantToPlayAgain = wantToPlayAgain;
    }

    @FXML
    public void onSquareClicked(MouseEvent event ) {

        if (!isNowMyTurn() || isGameEnded) {
            return;
        }

        int squareNumber = Integer.parseInt(((Pane)event.getSource()).getId().substring(6));

        try{

            if (board.isSquareEngaged(squareNumber)) {
                resetClicks();
                lastClicked = squareNumber;
                if(!board.getHitContinuation())
                    board.updatePossibleMoves();
                Pawn pawn = board.getPawn(board.getPawnNumber(squareNumber));
                possibleMoves = pawn.getPossibleMovesAssIntegers();
                for(Integer number:possibleMoves){
                    Circle possibleMoveMarker = new Circle(27, 27,15, Color.BLUE);
                    squaresMap.get(number).getChildren().add( possibleMoveMarker );
                }
            }
            else if(possibleMoves.contains(squareNumber)) {
                /*
                * In this case move is executed
                 */
                int movedPawnNumber = board.getPawnNumber(lastClicked);

                Color movedPawnColor = board.isPawnWhite(movedPawnNumber) ? Color.WHITE : Color.BLACK;

                board.moveFromSquareToSquare(lastClicked, squareNumber);

                squaresMap.get(lastClicked).getChildren().remove(1);

                logTextArea.insertText(logTextArea.getLength(), createMoveLog(lastClicked, squareNumber, true));
                resetClicks();
                possibleMoves = board.getPawn(movedPawnNumber).getPossibleMovesAssIntegers();

                Boolean isHitContinuation = !possibleMoves.isEmpty();

                connectionController.sendMessage(new MoveParameters(movedPawnNumber, squareNumber, isHitContinuation).toString());

                if(!isHitContinuation) {
                    if(!board.changePlayer()){
                        incomingMessagesReader = null;
                        System.out.println("Game over");
                        showNewGameQuestionWindow();
                    }
                    updateWhoMoveLabel();
                }


                Vector<Integer> squaresToClean = board.getHitsInLastMove();
                for (Integer squareToClean : squaresToClean) {
                    squaresMap.get(squareToClean).getChildren().remove(1);
                    addHitPawn(1);
                }

                putPawnOnSquare(movedPawnNumber, squareNumber, movedPawnColor);

            }
            else{
                resetClicks();
            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @FXML
    public void onSquareEntered(MouseEvent event ){
        int squareNumber = Integer.parseInt(((Pane)event.getSource()).getId().substring(6));
        try {
            if (board.isSquareEngaged(squareNumber)) {
                addHighlightToSquare(squareNumber);
            }
        }catch(Exception e){
            e.printStackTrace();
        }

    };

    @FXML
    public void onSquareExited(MouseEvent event ){
        int squareNumber = Integer.parseInt(((Pane)event.getSource()).getId().substring(6));
        try {
            if (board.isSquareEngaged(squareNumber)) {
                removeHighlightFromSquare(squareNumber);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    private void resetClicks(){
        for (Integer number : possibleMoves) {
            if(squaresMap.get(number).getChildren().size()>1)
                squaresMap.get(number).getChildren().remove(1);
        }
        possibleMoves = new Vector<>();
        lastClicked = null;
    }

    void putPawnOnSquare(int pawnNumber, int squareNumber, Color pawnColor){

        Circle pawnVisual = new PawnCircle(board.isQueen(pawnNumber), pawnColor);

        squaresMap.get(squareNumber).getChildren().add( pawnVisual);
    }


    void addHighlightToSquare(int squareNumber){
        Circle highlight = new Circle(27,27,25, Color.GREEN);
        squaresMap.get(squareNumber).getChildren().add(1, highlight);
    }

    void removeHighlightFromSquare(int squareNumber){
        if(squaresMap.get(squareNumber).getChildren().size() > 2)
            squaresMap.get(squareNumber).getChildren().remove(1);
    }

    void removePawnFromSquare(int squareNumber){
        int numberOfObjectsOnSquare = squaresMap.get(squareNumber).getChildren().size(); // 1st objects (at index 0) is rectangle represents square
        if( numberOfObjectsOnSquare > 1)
            squaresMap.get(squareNumber).getChildren().remove(1, numberOfObjectsOnSquare);

    }

    void initSquaresMap(){
        squaresMap.put(0, square0);
        squaresMap.put(1, square1);
        squaresMap.put(2, square2);
        squaresMap.put(3, square3);
        squaresMap.put(4, square4);
        squaresMap.put(5, square5);
        squaresMap.put(6, square6);
        squaresMap.put(7, square7);
        squaresMap.put(8, square8);
        squaresMap.put(9, square9);
        squaresMap.put(10, square10);
        squaresMap.put(11, square11);
        squaresMap.put(12, square12);
        squaresMap.put(13, square13);
        squaresMap.put(14, square14);
        squaresMap.put(15, square15);
        squaresMap.put(16, square16);
        squaresMap.put(17, square17);
        squaresMap.put(18, square18);
        squaresMap.put(19, square19);
        squaresMap.put(20, square20);
        squaresMap.put(21, square21);
        squaresMap.put(22, square22);
        squaresMap.put(23, square23);
        squaresMap.put(24, square24);
        squaresMap.put(25, square25);
        squaresMap.put(26, square26);
        squaresMap.put(27, square27);
        squaresMap.put(28, square28);
        squaresMap.put(29, square29);
        squaresMap.put(30, square30);
        squaresMap.put(31, square31);
        squaresMap.put(32, square32);
        squaresMap.put(33, square33);
        squaresMap.put(34, square34);
        squaresMap.put(35, square35);
        squaresMap.put(36, square36);
        squaresMap.put(37, square37);
        squaresMap.put(38, square38);
        squaresMap.put(39, square39);
        squaresMap.put(40, square40);
        squaresMap.put(41, square41);
        squaresMap.put(42, square42);
        squaresMap.put(43, square43);
        squaresMap.put(44, square44);
        squaresMap.put(45, square45);
        squaresMap.put(46, square46);
        squaresMap.put(47, square47);
        squaresMap.put(48, square48);
        squaresMap.put(49, square49);
        squaresMap.put(50, square50);
        squaresMap.put(51, square51);
        squaresMap.put(52, square52);
        squaresMap.put(53, square53);
        squaresMap.put(54, square54);
        squaresMap.put(55, square55);
        squaresMap.put(56, square56);
        squaresMap.put(57, square57);
        squaresMap.put(58, square58);
        squaresMap.put(59, square59);
        squaresMap.put(60, square60);
        squaresMap.put(61, square61);
        squaresMap.put(62, square62);
        squaresMap.put(63, square63);
    }

    void realizeOtherPlayerMove(int movedPawnNumber, int destination){

        try{

            Color movedPawnColor = board.isPawnWhite(movedPawnNumber) ? Color.WHITE : Color.BLACK;
            int sourceSquare = board.getPawnPosition(movedPawnNumber);
            board.movePawnTo(movedPawnNumber, new Coordinates(destination));

            addLineToMoveLog(createMoveLog(sourceSquare, destination, false));

            Vector<Integer> squaresToClean = board.getHitsInLastMove();
            for (Integer squareToClean : squaresToClean) {
                System.out.println("Deleting pawn on " + squareToClean);
                removePawnFromSquare(squareToClean);
                addHitPawn(2);
            }

            removePawnFromSquare(sourceSquare);
            putPawnOnSquare(movedPawnNumber, destination, movedPawnColor);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    Boolean isNowMyTurn(){
        return activePlayer == board.getActivePlayer();
    }

    void updateWhoMoveLabel(){
        if(board.getGameEnded() || isGameEnded){
            whoMoveLabel.setText("Game Over");
            whoMoveLabel.setTextFill(Color.RED);
            return;
        }
        if(activePlayer == getBoard().getActivePlayer()){
            whoMoveLabel.setText("Now is your move");
            whoMoveLabel.setTextFill(Color.GREEN);
        }
        else{
            whoMoveLabel.setText("Wait for other player's move");
            whoMoveLabel.setTextFill(Color.BLUE);
        }
    }

    void whoMoveLabelSetDisconnected(){
        whoMoveLabel.setText("Other player has disconnected");
        whoMoveLabel.setTextFill(Color.RED);
    }

    void initIndexLabels(Boolean isPlayer1White){
        String characters = new String("ABCDEFGH");
        if(isPlayer1White) {
            Integer i = 8;
            for (Node label : leftIndexLabels.getChildren()) {
                ((Label) label).setText(i.toString());
                i--;
            }
            i = 8;
            for (Node label : rightIndexLabels.getChildren()) {
                ((Label) label).setText(i.toString());
                i--;
            }
            i = 0;
            for (Node label : upIndexLabels.getChildren()) {
                ((Label) label).setText(Character.valueOf(characters.charAt(i)).toString());
                i++;
            }
            i = 0;
            for (Node label : downIndexLabels.getChildren()) {
                ((Label) label).setText(Character.valueOf(characters.charAt(i)).toString());
                i++;
            }
        }
        else{
            Integer i = 1;
            for (Node label : leftIndexLabels.getChildren()) {
                ((Label) label).setText(i.toString());
                i++;
            }
            i = 1;
            for (Node label : rightIndexLabels.getChildren()) {
                ((Label) label).setText(i.toString());
                i++;
            }
            i = 7;
            for (Node label : upIndexLabels.getChildren()) {
                ((Label) label).setText(Character.valueOf(characters.charAt(i)).toString());
                i--;
            }
            i = 7;
            for (Node label : downIndexLabels.getChildren()) {
                ((Label) label).setText(Character.valueOf(characters.charAt(i)).toString());
                i--;
            }

        }
    }

    String createMoveLog(int moveSource, int moveDestination, Boolean isItYourMove){
        String result = isItYourMove ? "Your move: " : "Other player's move: ";
        result += new SquareIndex(moveSource,isPlayer1White) + "->" + new SquareIndex(moveDestination,isPlayer1White) + "\n";
        return result;
    }

    void addLineToMoveLog(String log){
        logTextArea.insertText(logTextArea.getLength(), log);
    }

    void addHitPawn(int playerNumber){
        Color pawnColor;
        if(!isPlayer1White)
            pawnColor = playerNumber == 1 ? Color.WHITE : Color.BLACK;
        else
            pawnColor = playerNumber == 1 ? Color.BLACK : Color.WHITE;

        if(playerNumber == 1){
            player1HitPawns.getChildren().add(new Circle(18,pawnColor));
        }
        else{
            player2HitPawns.getChildren().add(new Circle(18,pawnColor));
        }
    }


    public void showNewGameQuestionWindow(){
        Stage stage = new Stage(StageStyle.UTILITY);
        stage.initOwner(boardVisual.getScene().getWindow());
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("NewGameQuestion.fxml"));
            Parent root = loader.load();

            NewGameQuestionController newGameQuestionController = loader.getController();

            newGameQuestionController.setBoardController(this);

            stage.setScene(new Scene(root));

            stage.show();

        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public Boolean getGameEnded() {
        return isGameEnded;
    }

    public Boolean getPlayer1White() {
        return isPlayer1White;
    }

    public void setPlayer1White(Boolean player1White) {
        isPlayer1White = player1White;
    }

    void setOnExit(){
        boardVisual.getScene().getWindow().setOnCloseRequest( e -> {
            try {
                if(incomingMessagesReader != null) {
                    incomingMessagesReader.interrupt();
                    connectionController.sendMessage("disconnecting");
                }
            }catch (Exception ex){
                ex.printStackTrace();
            }
        });
    }

}

