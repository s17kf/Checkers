package s17kf.checkers.Client;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

/**
 * Created by stefan on 06.06.17.
 */
public class NewGameQuestionController {

    @FXML
    Button yesButton, noButton;

    private BoardController boardController;

    public void setBoardController(BoardController boardController){
        this.boardController = boardController;
    }

    @FXML
    void yesClicked() throws Exception{

        boardController.setPlayer1White(!boardController.getPlayer1White());

        boardController.setWantToPlayAgain(true);

        Stage stage = (Stage)yesButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    void noClicked(){

        boardController.setWantToPlayAgain(false);

        Stage stage = (Stage)noButton.getScene().getWindow();
        stage.close();
    }




}
