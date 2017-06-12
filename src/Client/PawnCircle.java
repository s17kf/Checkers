package Client;

import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;

/**
 * Created by Stefan on 2017-05-31.
 */
public class PawnCircle extends Circle {

    PawnCircle(Boolean isQueen, Color pawnColor){
        super(27, 27,22, pawnColor);
//        Image image = new Image("Client/queen.png",)
        if(isQueen) {
            String imageURL = pawnColor == Color.WHITE ? "Client/View/whiteQueen.png" : "Client/View/blackQueen.png";
            setFill(new ImagePattern(new Image(imageURL)));
        }
    }


}
