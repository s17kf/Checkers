package Server;

import GameLogic.Chessboard;

import java.io.*;
import java.net.*;


/**
 * Created by Stefan on 2017-05-20.
 */

public class Server implements Runnable {

    private ServerSocket serverSocket;
    Socket players[];
    Chessboard board;

    public Server(int port) throws Exception {

        serverSocket = new ServerSocket(port);
        serverSocket.setSoTimeout(60000);

        players = new Socket[2];

        board = new Chessboard(true);

        System.out.println("Server started");
    }

    @Override
    public void run() {



        try {
            players[0] = serverSocket.accept();
        }catch(SocketTimeoutException e){
            System.out.println("Waiting time reached");
            return;
        }
        catch(Exception e){
            e.printStackTrace();
            return;
        }

        System.out.println("player1 address: " + players[0].getRemoteSocketAddress());

        while (true) {
            try {
                DataInputStream in = new DataInputStream(players[0].getInputStream());
                String recivedMessage = in.readUTF();

                System.out.println("incoming: " + recivedMessage);
                DataOutputStream out = new DataOutputStream(players[0].getOutputStream());
         //       out.writeUTF("Thanks for conecting to " + server[0].getLocalAddress());

                if(recivedMessage.equals("end connection")){
                    players[0].close();
                    break;
                }

            }catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }


        System.out.println("Outside while( true ) loop !");

    }

}

