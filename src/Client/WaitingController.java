package Client;

import Server.Server;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;



/**
 * Created by Stefan on 2017-05-19.
 */
public class WaitingController {

    private String playerName;

    @FXML
    private Label waitingLabel1, waitingLabel2;

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public void initLabels(){
        waitingLabel1.setText("Hello " + playerName + " now you must wait for second player");
        waitingLabel2.setText("Your IP adress is ..." + "\nSend it to your friend to play together");

    }

    public void setConnection(String portNumber, String ipAddress, ActionEvent event) throws  Exception{
        int port = Integer.parseInt(portNumber);
        Thread server = new Thread(new Server(port));
        server.start();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("Chessboard.fxml"));
        Parent root = loader.load();
        BoardController controller = loader.getController();
        controller.initBoard(ipAddress, portNumber, playerName);

        Scene boardScene = new Scene(root);
        Stage boardStage = (Stage)( waitingLabel1.getScene().getWindow());
        boardStage.setScene(boardScene);

        boardStage.show();

        controller.setOnExit();
    }


}

