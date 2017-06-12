package Server;

import GameLogic.Coordinates;
import GameLogic.MoveParameters;
import GameLogic.PrimitiveChessboard;

import java.io.*;
import java.net.*;
import java.util.Random;


/**
 * Created by Stefan on 2017-05-20.
 */

public class Server implements Runnable {

    private ServerSocket serverSocket;
    Socket players[];
    PrimitiveChessboard board;
    int activePlayer;
    Boolean isGameEnded;

    public Server(int port) throws Exception {

        serverSocket = new ServerSocket(port);
        serverSocket.setSoTimeout(20000);

        players = new Socket[2];

        System.out.println("Server started");

        isGameEnded = true;
        /*
        *   TODO musi byc false, ale najpeirw klient musi moc obslozyc koniec gry normalnie
         */
    }

    @Override
    public void run() {

        try {
            players[0] = serverSocket.accept();
            System.out.println("player1 connected, address: " + players[0].getRemoteSocketAddress());
            players[1] = serverSocket.accept();
            System.out.println("player2 connected, address: " + players[1].getRemoteSocketAddress());
        }catch(SocketTimeoutException e){
            System.out.println("Waiting time reached!");
            return;
        }
        catch(Exception e){
            e.printStackTrace();
            return;
        }

        Integer firstPlayerNumber = new Integer(1);

        try {
            Random rand = new Random();
            firstPlayerNumber = (rand.nextInt(50) % 2) +1;
            DataOutputStream out[] = new DataOutputStream[2];
            for(int i = 0;i < 2 ; i++)
                out[i]= new DataOutputStream(players[i].getOutputStream());
            out[0].writeUTF(firstPlayerNumber.toString());
            out[1].writeUTF(new Integer((firstPlayerNumber % 2) + 1).toString());
        }catch (IOException e){
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
        outside:do {
            try {
                board = new PrimitiveChessboard(firstPlayerNumber.equals(1));
                activePlayer = new Integer(firstPlayerNumber);
            }catch (Exception e){
                e.printStackTrace();
            }
            while (true) {
                try {
                    activePlayer = board.getActivePlayer();
                    DataInputStream in = new DataInputStream(players[activePlayer - 1].getInputStream());
                    String receivedMessage = in.readUTF();

                    System.out.println("incoming: " + receivedMessage);

                    if (receivedMessage.equals("disconnecting")) {
                        players[activePlayer - 1].close();
                        changeActivePlayer();
                        DataOutputStream out = new DataOutputStream(players[activePlayer - 1].getOutputStream());
                        out.writeUTF("disconnected");
                        break outside;
                    }
                    MoveParameters moveParameters = new MoveParameters(activePlayer, receivedMessage);


                    System.out.println("decoded: " + moveParameters);
                    board.movePawnTo(moveParameters.getMovedPawnNumber(), new Coordinates(moveParameters.getMoveDestination()));
//                System.out.println(board);
                    if (activePlayer == 1)
                        moveParameters.playerChange();

                    if (moveParameters.getHitContinuation()) {
                        changeActivePlayer();
                        DataOutputStream out = new DataOutputStream(players[activePlayer - 1].getOutputStream());
                        System.out.println("sent to: " + activePlayer);
                        out.writeUTF(moveParameters.toString());
                        changeActivePlayer();
                    } else {
                        if (!board.changePlayer()) {
                            System.out.println("Server: Game over!");
                            changeActivePlayer();
                            DataOutputStream out = new DataOutputStream(players[activePlayer - 1].getOutputStream());
                            out.writeUTF("endOfGame");
                            out.writeUTF(moveParameters.toString());
                            break;
                        }
                        changeActivePlayer();
                        DataOutputStream out = new DataOutputStream(players[activePlayer - 1].getOutputStream());
                        out.writeUTF(moveParameters.toString());
                    }


                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
            }

            String message1, message2;

            try {
                message1 = new String(new DataInputStream(players[0].getInputStream()).readUTF());
                System.out.println("Server received1: " + message1);
                message2 = new String(new DataInputStream(players[1].getInputStream()).readUTF());
                System.out.println("Server received2: " + message2);
                new DataOutputStream(players[1].getOutputStream()).writeUTF(message1);
                new DataOutputStream(players[0].getOutputStream()).writeUTF(message2);
                isGameEnded = true;
                if(message1.equals("true") && message2.equals("true")){
                    isGameEnded=false;
                }
            }catch (IOException e){
                isGameEnded = true;
                e.printStackTrace();
            }
            /*
            *   TODO zebranie informacji czy klienci chca dalej grac
             */


            firstPlayerNumber=(firstPlayerNumber % 2) + 1;
        }while (!isGameEnded);

        try {
            players[0].close();
            players[1].close();
            System.out.println("Outside while( true ) loop !");
        }catch (IOException e ){
            e.printStackTrace();
        }
    }

    void changeActivePlayer(){
        activePlayer = activePlayer % 2 + 1;
    }


}

