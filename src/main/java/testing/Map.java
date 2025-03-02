package testing;

import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;

public class Map extends GridPane {
    private static final int ROWS = 28;
    private static final int COLS = 58;
    private static final int TILE_SIZE = 30;

    public Map() {
        setPrefSize(COLS * TILE_SIZE, ROWS * TILE_SIZE); // Set fixed size
        setMinSize(COLS * TILE_SIZE, ROWS * TILE_SIZE);
        setMaxSize(COLS * TILE_SIZE, ROWS * TILE_SIZE);

        generateMap();
    }

    private void generateMap() {
        //Image grassTexture = new Image("images/grass.png"); // Example texture
        Image empty = null;

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                Tile tile = new Tile(row, col, 1, TILE_SIZE);
                add(tile, col, row);
            }
        }
    }
}
