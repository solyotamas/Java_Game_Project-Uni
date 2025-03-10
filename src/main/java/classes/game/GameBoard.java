package classes.game;

import classes.terrains.Ground;
import classes.terrains.Hill;
import classes.terrains.Terrain;
import javafx.scene.layout.Pane;
import java.util.Random;

public class GameBoard {
    private static final int TILE_SIZE = 30;
    private int TILE_COUNT_X;
    private int TILE_COUNT_Y;
    private final Pane gamePane;

    public GameBoard(Pane gamePane) {
        this.gamePane = gamePane;
    }

    public void loadLevel() {
        Random rand = new Random();
        TILE_COUNT_X = (int) (gamePane.getWidth() / TILE_SIZE);
        TILE_COUNT_Y = (int) (gamePane.getHeight() / TILE_SIZE);

        for (int y = 0; y < TILE_COUNT_Y; y++) {
            for (int x = 0; x < TILE_COUNT_X; x++) {
                Terrain terrain = getTileAt(x, y, rand);
                terrain.draw(gamePane);
            }
        }
    }

    private Terrain getTileAt(int x, int y, Random rand) {
        if (rand.nextInt(200) < 2) {
            addHillCluster(x, y);
            return new Hill(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE);
        }
        return new Ground(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE);
    }

    private void addHillCluster(int startX, int startY) {
        Random rand = new Random();
        int clusterSize = rand.nextInt(3) + 1;
        int placed = 0;

        for (int y = Math.max(0, startY - 1); y <= Math.min(TILE_COUNT_Y - 1, startY + 1) && placed < clusterSize; y++) {
            for (int x = Math.max(0, startX - 1); x <= Math.min(TILE_COUNT_X - 1, startX + 1) && placed < clusterSize; x++) {
                Terrain hillTerrain = new Hill(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE);
                hillTerrain.draw(gamePane);
                placed++;
            }
        }
    }
}