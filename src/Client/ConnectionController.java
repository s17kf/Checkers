package Client;

import GameLogic.MoveParameters;
import javafx.application.Platform;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
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
        System.out.println(1 + ": " + address.toString() + " " + port);
        server = new Socket(address,port);
        System.out.println(2);
        this.board = board;

    }

    void sendMessage(String message) throws Exception{

        DataOutputStream out = new DataOutputStream(server.getOutputStream());

        out.writeUTF(message);

    }

    String readMessage() throws Exception{
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
        while(board.incomingMessagesReader != null){
            try{
                if(!board.isNowMyTurn()) {
                /*
                * TODO jakiegos ifa bo wywala wyjatek jak ja klikne end connection
                * ??? Moze ten co na gorze wystarczy??
                * */
                    String receivedMessage = readMessage();

                    System.out.println("Client received: " + receivedMessage);

                    if (receivedMessage.equals("disconnected")) {
                        System.out.println("Other player has disconnected");
                        board.whoMoveLabelSetDisconnected();
                        board.addLineToMoveLog("Other player has disconnected!");
                        break;
                    }
                    if (receivedMessage.equals("endOfGame")) {
                        String lastMoveParameters = readMessage();
                        Platform.runLater(() -> {
                            realizeLastMove(lastMoveParameters);
                            board.updateWhoMoveLabel();
                        });

    /*
    * TODO trzeba opracowac zakonczenie gry do jednego z graczy wyslac pakiet o ruchu przeciwnika
     */

                        System.out.println("Game over");
                        break;
                    }
//                    Platform.runLater(() -> realizeLastMove(receivedMessage));
                    Platform.runLater(() -> {
                        realizeLastMove(receivedMessage);
                        board.updateWhoMoveLabel();
                    });
                }
                else {
                    board.incomingMessagesReader.sleep(250);
                }
            }catch (EOFException e) {
                if(board.getBoard().getGameEnded())
                    return;
                e.printStackTrace();
            }catch (Exception e){
                e.printStackTrace();
            }
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
