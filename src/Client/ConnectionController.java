package Client;

import GameLogic.MoveParameters;
import javafx.application.Platform;

import java.io.DataInputStream;
import java.io.DataOutputStream;
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

        server = new Socket(address,port);
        this.board = board;

    }

    void sendMessage(String message) throws Exception{

        DataOutputStream out = new DataOutputStream(server.getOutputStream());

        out.writeUTF(message);

    }

    String readMessage() throws Exception{
        DataInputStream in = new DataInputStream(server.getInputStream());

        return in.readUTF();
    }

    @Override
    public void run() {
        while(board.incomingMessagesReader != null){
            try{
                String receivedMessage = readMessage();

                System.out.println("Client received: " + receivedMessage);

                if (receivedMessage.equals("disconnected")) {
                    System.out.println("Other player has disconnected");
                    break;
                }
                if(receivedMessage.equals("endOfGame")){
//                                opponentMove = new MoveParameters(1, receivedMessage);
//                                opponentMovesToDo.set(true);
                    Platform.runLater( () -> realizeLastMove(receivedMessage) );
                    System.out.println("Game over");

    /*
    * TODO trzeba opracowac zakonczenie gry do jednego z graczy wyslac pakiet o ruchu przeciwnika
     */

                    break;
                }
                Platform.runLater( () -> realizeLastMove(receivedMessage) );


            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    void realizeLastMove(String receivedMessage){
        MoveParameters moveParameters = new MoveParameters(1, receivedMessage);
        board.realizeOtherPlayerMove(moveParameters.getMovedPawnNumber(), moveParameters.getMoveDestination());
//                            opponentMove = new MoveParameters(1, receivedMessage);
//                            opponentMovesToDo.set(true);
        if(!moveParameters.getHitContinuation()){
            try {
                board.getBoard().changePlayer();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

}
