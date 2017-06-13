package s17kf.checkers.Client;

import s17kf.checkers.Server.Server;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

/**
 * Created by Stefan on 2017-05-19.
 */
public class LoginController {

    @FXML
    private TextField  addressIP, port1, port2;

    @FXML
    public void onCreateGameClick (ActionEvent event) throws Exception {
        System.out.println("Button clicked ");

        try {
            int port = Integer.parseInt(port1.getText());
            Thread server = new Thread(new Server(port));
            server.start();


            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("Chessboard.fxml"));
            Parent root = loader.load();
            BoardController controller = loader.getController();
            controller.initBoard("localhost", port1.getText());

            Scene boardScene = new Scene(root);
            Stage boardStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            boardStage.setScene(boardScene);

            boardStage.show();

            controller.setOnExit();
        }catch (NumberFormatException e) {
            Platform.runLater(() -> windowForNumberFormatExceptionShow());
        }catch (SocketTimeoutException e){
            Platform.runLater( () -> {
                Stage stage = new Stage(StageStyle.UTILITY);
                stage.initOwner(addressIP.getScene().getWindow());

                Label infoLabel = new Label("Nobody has connected to your game, try again!");

                Button okButton = new Button("OK");

                BorderPane root = new BorderPane();

                root.setTop(infoLabel);
                root.setCenter(okButton);

                okButton.setOnAction(a -> {
                    stage.close();
                });

                stage.setScene(new Scene(root));

                stage.show();
            });
        }


    }


    public void onJoinClick(ActionEvent event ){
        System.out.println("Button2 clicked ");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("Chessboard.fxml"));
            Parent root = loader.load();
            BoardController controller = loader.getController();
            controller.initBoard(addressIP.getText(), port2.getText());

            Scene boardScene = new Scene(root);
            Stage boardStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            boardStage.setScene(boardScene);

            boardStage.show();

            controller.setOnExit();
        }catch (ConnectException e){
            Platform.runLater(() -> {
                Stage stage = new Stage(StageStyle.UTILITY);
                stage.initOwner(addressIP.getScene().getWindow());

                Label infoLabel = new Label("Connection Error!");

                Button okButton = new Button("OK");



                BorderPane root = new BorderPane();

                root.setTop(infoLabel);
                root.setCenter(okButton);

//                okButton.setAlignment(root.getBottom().get);

                okButton.setOnAction(a -> {
                    stage.close();
                });

                stage.setScene(new Scene(root));

                stage.show();


            });
        }catch (NumberFormatException e) {
            Platform.runLater(() -> windowForNumberFormatExceptionShow());
        }catch (UnknownHostException e) {
            Platform.runLater(() -> {
                Stage stage = new Stage(StageStyle.UTILITY);
                stage.initOwner(addressIP.getScene().getWindow());

                Label infoLabel = new Label("Unknown IP address!");

                Button okButton = new Button("OK");

                BorderPane root = new BorderPane();

                root.setTop(infoLabel);
                root.setCenter(okButton);

                okButton.setOnAction(a -> {
                    stage.close();
                });

                stage.setScene(new Scene(root));

                stage.show();


            });
        }catch(SocketException e) {
            Stage stage = new Stage(StageStyle.UTILITY);
            stage.initOwner(addressIP.getScene().getWindow());

            Label infoLabel = new Label(e.getMessage());

            Button okButton = new Button("OK");

            BorderPane root = new BorderPane();

            root.setTop(infoLabel);
            root.setCenter(okButton);

            okButton.setOnAction(a -> {
                stage.close();
            });

            stage.setScene(new Scene(root));

            stage.show();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    void windowForNumberFormatExceptionShow(){
        Stage stage = new Stage(StageStyle.UTILITY);
        stage.initOwner(addressIP.getScene().getWindow());

        Label infoLabel = new Label("Wrong port number type!");

        Button okButton = new Button("OK");

        BorderPane root = new BorderPane();

        root.setTop(infoLabel);
        root.setCenter(okButton);

        okButton.setOnAction(a -> {
            stage.close();
        });

        stage.setScene(new Scene(root));

        stage.show();

    }

}


