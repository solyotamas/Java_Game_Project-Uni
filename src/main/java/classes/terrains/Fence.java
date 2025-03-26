package classes.terrains;

import javafx.scene.image.Image;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.Random;

public class Fence extends Terrain {

    private static final ArrayList<Image> fenceImages = new ArrayList<>();
    private static final Random rand = new Random();

    public Fence(int row, int col) {
        super(row, col, getRandomFenceImage(), 0);
    }

    private static Image getRandomFenceImage() {
        return fenceImages.get(rand.nextInt(fenceImages.size()));
    }

    static {
        for (int i = 1; i <= 3; i++) {
            String path = "/images/fence" + i + ".png";
            fenceImages.add(new Image(Fence.class.getResource(path).toExternalForm()));
        }
    }
}
