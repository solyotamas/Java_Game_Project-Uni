package classes.terrains;

import javafx.scene.image.Image;

public class Entrance extends Terrain {

    private static Image entranceImage;

    public Entrance(int row, int col) {
        super(row, col, entranceImage, 0, false);
    }

    static {
        entranceImage = new Image(Entrance.class.getResource("/images/entrance.png").toExternalForm());
    }

}
