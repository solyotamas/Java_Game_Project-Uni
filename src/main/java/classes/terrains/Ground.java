package classes.terrains;

import java.util.ArrayList;
import java.util.Random;
import javafx.scene.image.Image;

public class Ground extends Terrain {

    private static final ArrayList<Image> groundImages = new ArrayList<>();
    private static final Random rand = new Random();

    public Ground(int row, int col) {
        super(row, col, getRandomGrassImage(), 1);
    }

    private static Image getRandomGrassImage() {
        return groundImages.get(rand.nextInt(groundImages.size()));
    }

    //preload
    static {
        for (int i = 1; i <= 4; i++) {
            String path = "/images/grass" + i + ".png";
            groundImages.add(new Image(Ground.class.getResource(path).toExternalForm()));
        }
    }
}