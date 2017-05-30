package Server;

import GameLogic.Chessboard;
import GameLogic.Coordinates;
import GameLogic.MoveParameters;
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
    int activePlayer;

    public Server(int port) throws Exception {

        serverSocket = new ServerSocket(port);
        serverSocket.setSoTimeout(15000);

        players = new Socket[2];

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
            board = new PrimitiveChessboard(false);

            DataOutputStream out[] = new DataOutputStream[2];
            for(int i = 0;i < 2 ; i++)
                out[i]= new DataOutputStream(players[i].getOutputStream());
            out[0].writeUTF("2");
            out[1].writeUTF("1");
            activePlayer = 2;
        }catch (IOException e){
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }

        while (true) {
            try {
                activePlayer = board.getActivePlayer();
                DataInputStream in = new DataInputStream(players[activePlayer-1].getInputStream());
                String receivedMessage = in.readUTF();

                System.out.println("incoming: " + receivedMessage);

                if(receivedMessage.equals("disconnecting")){
                    players[activePlayer-1].close();
                    changeActivePlayer();
                    DataOutputStream out = new DataOutputStream(players[activePlayer-1].getOutputStream());
                    out.writeUTF("disconnected");
                    break;
                }
//                Object moveParameters[] = getMoveParameters(activePlayer, receivedMessage);
                MoveParameters moveParameters = new MoveParameters(activePlayer, receivedMessage);

//                int movedPawnNumber = (Integer)moveParameters[0], moveDestination = (Integer)moveParameters[1];
//                Boolean isHitContinuation = (Boolean) moveParameters[2];

                System.out.println("decoded: " + moveParameters);
                board.movePawnTo(moveParameters.getMovedPawnNumber(),new Coordinates(moveParameters.getMoveDestination()));
//                System.out.println(board);
                if(activePlayer == 1)
                    moveParameters.playerChange();
                if(moveParameters.getHitContinuation()){
                    changeActivePlayer();
                    DataOutputStream out = new DataOutputStream(players[activePlayer-1].getOutputStream());
                    System.out.println("sent to: " + activePlayer);
                    out.writeUTF(moveParameters.toString());
                    changeActivePlayer();
                }
                else {
                    Boolean isGameEnded = !board.changePlayer();

                    if(isGameEnded){
                        DataOutputStream out = new DataOutputStream(players[activePlayer-1].getOutputStream());
                        out.writeUTF("endOfGame");
                        changeActivePlayer();
                        out = new DataOutputStream(players[activePlayer-1].getOutputStream());
                        out.writeUTF("endOfGame");
                        out.writeUTF(moveParameters.toString());
                        break;
                    }

//                    changeActivePlayer(activePlayer);
                    activePlayer=board.getActivePlayer();
//                    if(activePlayer == 2){
//                        moveParameters.playerChange();
//                    }
                    DataOutputStream out = new DataOutputStream(players[activePlayer-1].getOutputStream());
                    out.writeUTF(moveParameters.toString());
                }





            }catch (IOException e) {
                e.printStackTrace();
                break;
            }catch (Exception e){
                e.printStackTrace();
                break;
            }
        }


        System.out.println("Outside while( true ) loop !");

    }

    void changeActivePlayer(){
        activePlayer = activePlayer % 2 + 1;
    }

//    private Object[] getMoveParameters(int playerNumber, String message) {
//        Integer movedPawnNumber, moveDestination;
//        Boolean isHitContinuation;
//        Object[] result = new Object[3];
//        int i = 0;
//        String part1 = new String(), part2 = new String(), part3 = new String();
//        for (; message.charAt(i) != ';'; i++){
//            part1 += message.charAt(i);
//        }
//        for(i++; message.charAt(i) != ';'; i++){
//            part2 += message.charAt(i);
//        }
//        for(i++; i < message.length(); i++){
//            part3 += message.charAt(i);
//        }
//        movedPawnNumber = Integer.parseInt(part1);
//        moveDestination = Integer.parseInt(part2);
//        isHitContinuation = Boolean.parseBoolean(part3);
//        if(playerNumber == 2){
//            movedPawnNumber = movedPawnNumber < 12 ? movedPawnNumber + 12 : movedPawnNumber - 12;
//            moveDestination = 63 - moveDestination;
//        }
//        result[0] = movedPawnNumber;
//        result[1] = moveDestination;
//        result[2] = isHitContinuation;
//        return result;
//    }

}

