package classes.terrains;

import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.Random;

public class Floor extends Terrain{
    private static final ArrayList<Image> floorImages = new ArrayList<>();
    private static final Random rand = new Random();

    public Floor(int row, int col) {
        super(row, col, getRandomFloorImage(), 0);
    }

    private static Image getRandomFloorImage() {
        return floorImages.get(rand.nextInt(floorImages.size()));
    }

    //preload
    public static void preloadFloorImages() {
        for (int i = 1; i <= 3; i++) {
            String path = "/images/road" + i + ".png";
            floorImages.add(new Image(Floor.class.getResource(path).toExternalForm()));
        }
    }

}
