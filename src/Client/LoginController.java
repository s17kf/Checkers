package Client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Created by Stefan on 2017-05-19.
 */
public class LoginController {

    @FXML
    private Button gameCreateButton, joinButton;
    @FXML
    private TextField name1, name2, addressIP, port1, port2;


    @FXML
    public void onCreateGameClick (ActionEvent event) throws Exception {
        System.out.println("Button clicked "+name1.getText());

        FXMLLoader loader = new FXMLLoader(getClass().getResource("PlayerWaiting.fxml"));
        Parent root = loader.load();
        WaitingController controller = loader.getController();
        controller.setPlayerName(name1.getText());
        controller.initLabels();

        Scene waitingScene = new Scene(root);
        Stage waitingStage = (Stage)((Node) event.getSource()).getScene().getWindow();

        waitingStage.setScene(waitingScene);

        waitingStage.show();

        controller.setConnection(port1.getText(), "localhost", event);

     //   WaitingController.initLabels(name1.getText());

    }


    public void onJoinClick(ActionEvent event ) throws Exception {
        System.out.println("Button2 clicked "+name2.getText());

        FXMLLoader loader = new FXMLLoader(getClass().getResource("Chessboard.fxml"));
        Parent root = loader.load();
        BoardController controller = loader.getController();
        controller.initBoard(addressIP.getText(),port2.getText());

        Scene boardScene = new Scene(root);
        Stage boardStage = (Stage)((Node) event.getSource()).getScene().getWindow();
        boardStage.setScene(boardScene);



        boardStage.show();
    }


}
