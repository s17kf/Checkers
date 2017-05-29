package Client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by Stefan on 2017-05-22.
 */
public class ConnectionController implements Runnable{

    Socket server;

    ConnectionController(String ipAddress, String portNumber) throws Exception{
        InetAddress address = InetAddress.getByName(ipAddress);
        int port = Integer.parseInt(portNumber);

        server = new Socket(address,port);

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
        String readedMessage;
        while(true){
            try{
                readedMessage = readMessage();

                if(readedMessage.equals("end")){

                    break;
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
