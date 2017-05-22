package Client;

import GameLogic.Chessboard;
import GameLogic.Pawn;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.input.MouseEvent;
import javafx.fxml.FXML;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;


/**
 * Created by Stefan on 2017-05-19.
 */
public class BoardController {

    @FXML
    private GridPane boardVisual;

    @FXML
    Rectangle square0, square1, square2, square3, square4, square5, square6, square7, square8, square9, square10, square11, square12, square13, square14, square15, square16, square17, square18, square19, square20;
    @FXML
    Rectangle square21, square22, square23, square24, square25, square26, square27, square28, square29, square30, square31, square32, square33, square34, square35, square36, square37, square38, square39, square40;
    @FXML
    Rectangle square41, square42, square43, square44, square45, square46, square47, square48, square49, square50, square51, square52, square53, square54, square55, square56, square57, square58, square59, square60, square61, square62, square63;

    private Map<Integer, Rectangle> squaresMap;
    private Vector<Integer> possibleMoves;
    private Integer lastClicked;

//    private Circle pawnsVisual[];

    private Boolean isPlayer1White;
    private String player1Name, player2Name;
    private Chessboard board;
    private ConnectionController connectionController;

    public BoardController() throws Exception{
        board=new Chessboard(true);
//        pawnsVisual=new Circle[24];
        this.isPlayer1White=true;
        squaresMap = new HashMap<>();
        possibleMoves=new Vector<Integer>();
//        for(int i=0;i<12;i++){
//            pawnsVisual[i] = new Circle(22, Color.WHITE);
//            pawnsVisual[i].setCenterX(27);
//            pawnsVisual[i].setCenterY(27);
//
//            pawnsVisual[i].setOnMouseClicked( e -> {
//                onSquareClicked( e.copyFor(((Pane)((Circle)e.getSource()).getParent()).getChildren().get(0),e.getTarget()));
//            });
//            pawnsVisual[i].setOnMouseEntered( e -> {
//                onSquareEntered( e.copyFor(((Pane)((Circle)e.getSource()).getParent()).getChildren().get(0),e.getTarget()));
//            });
//            pawnsVisual[i].setOnMouseExited( e -> {
//                onSquareExited( e.copyFor(((Pane)((Circle)e.getSource()).getParent()).getChildren().get(0),e.getTarget()));
//            });
//        }
//        for(int i=12;i<24;i++){
//            pawnsVisual[i] = new Circle(22, Color.BLACK);
//            pawnsVisual[i].setCenterX(27);
//            pawnsVisual[i].setCenterY(27);
//
//            pawnsVisual[i].setOnMouseClicked( e -> {
//                onSquareClicked( e.copyFor(((Pane)((Circle)e.getSource()).getParent()).getChildren().get(0),e.getTarget()));
//            });
//            pawnsVisual[i].setOnMouseEntered( e -> {
//                onSquareEntered( e.copyFor(((Pane)((Circle)e.getSource()).getParent()).getChildren().get(0),e.getTarget()));
//            });
//            pawnsVisual[i].setOnMouseExited( e -> {
//                onSquareExited( e.copyFor(((Pane)((Circle)e.getSource()).getParent()).getChildren().get(0),e.getTarget()));
//            });
//        }
    }

    public void initBoard(String ipAddress, String portNumber) throws Exception{
        initSquaresMap();
        int colorInfluence = isPlayer1White?0:12;
        if(isPlayer1White){
            for(int i=0+colorInfluence;i<12+colorInfluence;i++){
                putPawnOnSquare(board.getPawn(i).getPosition().toInt(),Color.WHITE);
            }
            for(int i=12-colorInfluence;i<24-colorInfluence;i++){
                putPawnOnSquare(board.getPawn(i).getPosition().toInt(),Color.BLACK);
            }
        }

        connectionController = new ConnectionController(ipAddress,portNumber);
//        board.updatePossibleMoves();
    }

    public void setPlayer1Name(String player1Name) {
        this.player1Name = player1Name;
    }

    public void setPlayer2Name(String player2Name) {
        this.player2Name = player2Name;
    }

    public Chessboard getBoard() {
        return board;
    }

    @FXML
    public void onSquareClicked(MouseEvent event ) {

        int squareNumber = Integer.parseInt(((Shape)event.getSource()).getId().substring(6));

        try{
//            connectionController.sendMessage(squareNumber + " -> " + board.getPawnNumber(squareNumber));



            if (board.isSquareEngaged(squareNumber)) {
                resetClicks();
                lastClicked = squareNumber;
                Pawn pawn = board.getPawn(board.getPawnNumber(squareNumber));
                possibleMoves = pawn.getPossibleMovesAssIntegers();
                for(Integer number:possibleMoves){
                    Circle possibleMoveMarker = new Circle(27, 27,15, Color.BLUE);
                    ((Pane)squaresMap.get(number).getParent()).getChildren().add( possibleMoveMarker );
                    possibleMoveMarker.setOnMouseClicked( e -> {
                        onSquareClicked( e.copyFor(((Pane)((Circle)e.getSource()).getParent()).getChildren().get(0),e.getTarget()));
                    });
                    possibleMoveMarker.setOnMouseEntered( e -> {
                        onSquareEntered( e.copyFor(((Pane)((Circle)e.getSource()).getParent()).getChildren().get(0),e.getTarget()));
                    });
                    possibleMoveMarker.setOnMouseExited( e -> {
                        onSquareExited( e.copyFor(((Pane)((Circle)e.getSource()).getParent()).getChildren().get(0),e.getTarget()));
                    });
                }
            }
            else if(possibleMoves.contains(squareNumber)) {
                /*
                * In this case move is executed
                 */
                connectionController.sendMessage("move: " + lastClicked + "to: " + squareNumber);
                int movedPawnNumber = board.getPawnNumber(lastClicked);

                Color movedPawnColor = board.isPawnWhite(movedPawnNumber) ? Color.WHITE : Color.BLACK;

                board.moveFromSquareToSquare(lastClicked, squareNumber);

                Vector<Integer> squaresToClean = board.getHitsInLastMove();
                for (Integer squareToClean : squaresToClean) {
                    ((Pane) squaresMap.get(squareToClean).getParent()).getChildren().remove(1);
                }

                ((Pane) squaresMap.get(lastClicked).getParent()).getChildren().remove(1);

                resetClicks();
                putPawnOnSquare(squareNumber, movedPawnColor);


            }
            else{
                resetClicks();
            }

        }catch (Exception e){
            e.printStackTrace();
        }



//        System.out.println(((Shape)event.getSource()).getParent());
//        ((Pane)((Shape) event.getSource()).getParent()).getChildren());

    };

    @FXML
    public void onSquareEntered(MouseEvent event ){
        int squareNumber = Integer.parseInt(((Shape)event.getSource()).getId().substring(6));
        try {
            if (board.isSquareEngaged(squareNumber)) {
                Circle highlight = new Circle(27,27,25, Color.GREEN);
                ((Pane)((Shape) event.getSource()).getParent()).getChildren().add(1, highlight);
            }
        }catch(Exception e){
            e.printStackTrace();
        }

    };

    @FXML
    public void onSquareExited(MouseEvent event ){
        int squareNumber = Integer.parseInt(((Shape)event.getSource()).getId().substring(6));
        try {
            if (board.isSquareEngaged(squareNumber)) {
                if(((Pane)((Shape) event.getSource()).getParent()).getChildren().size()>2)
                    ((Pane)((Shape) event.getSource()).getParent()).getChildren().remove(1);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @FXML
    public void onEndButtonAction(ActionEvent event){
        try {
            connectionController.sendMessage("end connection");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void resetClicks(){
        for (Integer number : possibleMoves) {
            ((Pane) squaresMap.get(number).getParent()).getChildren().remove(1);
        }
        possibleMoves = new Vector<>();
        lastClicked = null;
    }

    void putPawnOnSquare(int squareNumber, Color pawnColor){
        Circle pawnVisual = new Circle(27, 27,22, pawnColor);
        pawnVisual.setOnMouseClicked( e -> {
            onSquareClicked( e.copyFor(((Pane)((Circle)e.getSource()).getParent()).getChildren().get(0),e.getTarget()));
        });
        pawnVisual.setOnMouseEntered( e -> {
            onSquareEntered( e.copyFor(((Pane)((Circle)e.getSource()).getParent()).getChildren().get(0),e.getTarget()));
        });
        pawnVisual.setOnMouseExited( e -> {
            onSquareExited( e.copyFor(((Pane)((Circle)e.getSource()).getParent()).getChildren().get(0),e.getTarget()));
        });
        ((Pane)squaresMap.get(squareNumber).getParent()).getChildren().add( pawnVisual);
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
}
