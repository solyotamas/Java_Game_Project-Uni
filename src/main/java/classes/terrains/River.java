package classes.terrains;

import javafx.scene.image.Image;

public class River extends Terrain {

    private static Image riverImage;

    public River(int x, int y) {
        super(x, y, riverImage, 2);
    }

    //preload
    public static void preloadRiverImage() {
        riverImage = new Image(River.class.getResource("/images/river.png").toExternalForm());
    }
}