package classes.terrains;

import java.util.ArrayList;
import java.util.Random;
import javafx.scene.image.Image;

public class Hill extends Terrain {

    private static final ArrayList<Image> hillImages = new ArrayList<>();
    private static final Random rand = new Random();

    public Hill(int x, int y) {
        super(x, y, getRandomHillImage(), 2);
    }

    private static Image getRandomHillImage() {
        return hillImages.get(rand.nextInt(hillImages.size()));
    }

    // Preload images
    public static void preloadHillImages() {
        for (int i = 1; i <= 7; i++) {
            String path = "/images/hill" + i + ".png";
            hillImages.add(new Image(Hill.class.getResource(path).toExternalForm()));
        }
    }
}