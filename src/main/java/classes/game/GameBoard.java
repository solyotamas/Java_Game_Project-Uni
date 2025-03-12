package classes.game;

import classes.terrains.Ground;
import classes.terrains.Hill;
import classes.terrains.Terrain;
import javafx.scene.layout.Pane;
import java.util.Random;

public class GameBoard{
    //stats
    private static final int ROWS = 31;
    private static final int COLUMNS = 64;
    private static final int TILE_SIZE = 30; // assumed tile size
    //representation
    private final Pane gamePane;
    private final Terrain[][] terrainGrid = new Terrain[COLUMNS][ROWS];
    //conf
    private final Random rand = new Random();

    public GameBoard(Pane gamePane) {
        this.gamePane = gamePane;
    }

    public void setupBoard() {

        for (int x = 0; x < COLUMNS; x++) {
            for (int y = 0; y < ROWS; y++) {
                makeTerrain(x, y);
            }
        }
    }

    private void makeTerrain(int x, int y) {
        int terrainType = rand.nextInt(100);

        Terrain terrain;
        if (terrainType < 2) // 2% chance for a hill
            terrain = new Hill(x, y);
        else
            terrain = new Ground(x, y);


        //Placing inside gamePane and keeping track inside the matrix
        gamePane.getChildren().add(terrain);
        terrainGrid[x][y] = terrain;
    }

    // #TODO to implement it still because it doesnt work yet
    private void addHillCluster(int startX, int startY) {
        Random rand = new Random();
        int clusterSize = rand.nextInt(3) + 1;
        int placed = 0;

        for (int y = Math.max(0, startY - 1); y <= Math.min(ROWS - 1, startY + 1) && placed < clusterSize; y++) {
            for (int x = Math.max(0, startX - 1); x <= Math.min(COLUMNS - 1, startX + 1) && placed < clusterSize; x++) {
                Terrain hillTerrain = new Hill(x, y);
                gamePane.getChildren().add(hillTerrain);
                placed++;
            }
        }
    }
}