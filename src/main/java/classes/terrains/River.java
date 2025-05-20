package classes.terrains;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;

public class River extends Terrain {

    private static Image riverImage;

    public River(int row, int col) {
        super(row, col, riverImage, 3, true);
    }

    static {
        riverImage = new Image(River.class.getResource("/images/river.png").toExternalForm());
    }
}