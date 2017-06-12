package Client;

import GameLogic.MoveParameters;
import com.sun.org.apache.xpath.internal.operations.Bool;
import javafx.application.Platform;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by Stefan on 2017-05-22.
 */
public class ConnectionController implements Runnable{

    Socket server;
    BoardController board;

    ConnectionController(String ipAddress, String portNumber, BoardController board) throws Exception{
        InetAddress address = InetAddress.getByName(ipAddress);
        int port = Integer.parseInt(portNumber);
//        System.out.println(1 + ": " + address.toString() + " " + port);
        server = new Socket(address,port);
//        System.out.println(2);
        this.board = board;

    }

    void sendMessage(String message) throws Exception{

        DataOutputStream out = new DataOutputStream(server.getOutputStream());

        out.writeUTF(message);

    }

    String readMessage() throws IOException{
//        try {
            DataInputStream in = new DataInputStream(server.getInputStream());

            return in.readUTF();
//        }catch (Exception e){
//            e.printStackTrace();
//        }
       // return null;
    }

    @Override
    public void run() {
        Boolean isDisconnected = false;
        board.setWantToPlayAgain(null);

        while(board.incomingMessagesReader != null){
            try{
                while (board.isNowMyTurn() && !board.getGameEnded()){
                    board.incomingMessagesReader.sleep(100);
                }
                board.incomingMessagesReader.sleep(100);

                if(board.getBoard().getGameEnded()){
                    System.out.println("Connection reader: Game ended, line 84");
                    break;
                }

                System.out.println("Incoming reader: wait for message line 68");
                String receivedMessage = readMessage();

                System.out.println("Client received: " + receivedMessage);

                if (receivedMessage.equals("disconnected")) {
                    isDisconnected = true;
                    System.out.println("Other player has disconnected");
                    Platform.runLater(() -> {
                        board.whoMoveLabelSetDisconnected();

                        board.addLineToMoveLog("Other player has disconnected!");
                    });
                    board.setWantToPlayAgain(false);
                    return;
                }
                if (receivedMessage.equals("endOfGame")) {
                    String lastMoveParameters = readMessage();
                    Platform.runLater(() -> {
                        realizeLastMove(lastMoveParameters);
                        board.updateWhoMoveLabel();
                        board.showNewGameQuestionWindow();
                    });


                    System.out.println("Game over");
                    break;
                }

                Platform.runLater(() -> {
                    realizeLastMove(receivedMessage);
                    board.updateWhoMoveLabel();
                });

                if (new MoveParameters(2, receivedMessage).getHitContinuation())
                    continue;


                while (!board.isNowMyTurn())
                    board.incomingMessagesReader.sleep(100);            // beacause of platform runLater -> releaseLastMove



//                System.out.println("end of while(true)");

            }catch (InterruptedException e){
                System.out.println("interrupted exception");
                return;
            }catch (IOException e){
                if(board.getBoard().getGameEnded() || isDisconnected || board.getGameEnded()){
                    System.out.println("IOException catch");
                    return;
                }
                Platform.runLater(() -> {
                    board.whoMoveLabelSetDisconnected();

                    board.addLineToMoveLog("Other player has disconnected!");
                });
                return;
            }
        }

        try{
            while (board.getWantToPlayAgain() == null){
                board.incomingMessagesReader.sleep(100);
            }

            System.out.println("Connection controller: my want to play value: " + board.getWantToPlayAgain());

            sendMessage(board.getWantToPlayAgain().toString());

            System.out.println("Incoming reader: wait for message line 135");
            String receivedMessage = new String(readMessage());
            System.out.println("Received value: " + receivedMessage);
            if (!receivedMessage.equals("true")){
                board.setWantToPlayAgain(false);
            }
        }catch (InterruptedException e){
            System.out.println("interrupted exception");
            return;
        }catch (IOException e){
            System.out.println("Why IOException");
            e.printStackTrace();
        }catch (Exception e){
            System.out.println("other exception");
            e.printStackTrace();
        }

        if(board.getWantToPlayAgain()){
            Platform.runLater( () -> {
                try {
                    board.createChessboard(board.getPlayer1White());
                }catch (Exception e){
                    e.printStackTrace();
                }
            });
        }
    }

    void realizeLastMove(String receivedMessage){
        MoveParameters moveParameters = new MoveParameters(1, receivedMessage);
        board.realizeOtherPlayerMove(moveParameters.getMovedPawnNumber(), moveParameters.getMoveDestination());
        if(!moveParameters.getHitContinuation()){
            try {
                board.getBoard().changePlayer();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

}
