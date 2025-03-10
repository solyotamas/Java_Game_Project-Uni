package classes.terrains;

import javafx.scene.image.Image;


public class Hill extends Terrain {

    private static Image hillImage;

    public Hill(int x, int y) {
        super(x, y, hillImage, 2);
    }

    //preload
    public static void preloadHillImage() {
        hillImage = new Image(Hill.class.getResource("/images/hill.png").toExternalForm());
    }
}