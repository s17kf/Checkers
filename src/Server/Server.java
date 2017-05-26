package Server;

import GameLogic.Chessboard;
import GameLogic.PrimitiveChessboard;

import java.io.*;
import java.net.*;


/**
 * Created by Stefan on 2017-05-20.
 */

public class Server implements Runnable {

    private ServerSocket serverSocket;
    Socket players[];
    PrimitiveChessboard board;

    public Server(int port) throws Exception {

        serverSocket = new ServerSocket(port);
        serverSocket.setSoTimeout(60000);

        players = new Socket[2];

        board = new PrimitiveChessboard(true);

        System.out.println("Server started");
    }

    @Override
    public void run() {

        try {
            players[0] = serverSocket.accept();
            System.out.println("player1 connected, address: " + players[0].getRemoteSocketAddress());
            players[1] = serverSocket.accept();
            System.out.println("player2 connected, address: " + players[1].getRemoteSocketAddress());
        }catch(SocketTimeoutException e){
            System.out.println("Waiting time reached");
            return;
        }
        catch(Exception e){
            e.printStackTrace();
            return;
        }



        try {
            DataOutputStream out[] = new DataOutputStream[2];
            for(int i = 0;i < 2 ; i++)
                out[i]= new DataOutputStream(players[0].getOutputStream());
            out[0].writeUTF("2");
            out[2].writeUTF("1");
        }catch (IOException e){
            e.printStackTrace();
        }

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

            }catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }


        System.out.println("Outside while( true ) loop !");

    }

}

