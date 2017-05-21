package Client;

import javafx.fxml.FXML;
import javafx.scene.control.Label;


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


}

