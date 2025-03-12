package classes.game;

import classes.terrains.*;
import classes.terrains.Terrain;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
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

        generateRiver(safeRandomX(), 0);
        generateRiver(safeRandomX(), 15);
        generateRiver(safeRandomX(), 23);
    }

    private int safeRandomX() {
        int margin = 5;
        return rand.nextInt(COLUMNS - 2 * margin) + margin;
    }

    private void makeTerrain(int x, int y) {
        int terrainType = rand.nextInt(1000);

        Terrain terrain;
        if (terrainType < 15) { // 2% chance for a hill
            terrain = new Hill(x, y);
            addHillCluster(x, y);
        } else {
            terrain = new Ground(x, y);
        }

        //Placing inside gamePane and keeping track inside the matrix
        gamePane.getChildren().add(terrain);
        terrainGrid[x][y] = terrain;
    }

    // #TODO to implement it still because it doesnt work yet
    private void addHillCluster(int startX, int startY) {
        int clusterSize = rand.nextInt(2) + 3;
        ArrayList<int[]> positions = new ArrayList<>();
        positions.add(new int[]{startX, startY});

        int placed = 0;
        while (placed < clusterSize && !positions.isEmpty()) {
            int[] pos = positions.remove(rand.nextInt(positions.size()));

            int x = pos[0];
            int y = pos[1];

            if (!(terrainGrid[x][y] instanceof Hill)) {
                Terrain hillTerrain = new Hill(x, y);
                gamePane.getChildren().add(hillTerrain);
                terrainGrid[x][y] = hillTerrain;
                placed++;

                if (x > 0) positions.add(new int[]{x - 1, y});
                if (x < COLUMNS - 1) positions.add(new int[]{x + 1, y});
                if (y > 0) positions.add(new int[]{x, y - 1});
                if (y < ROWS - 1) positions.add(new int[]{x, y + 1});
            }
        }
    }

    private void generateRiver(int startX, int startY) {
        int x = startX;
        int y = startY;

        int riverLength = rand.nextInt(15) + 20;
        int direction = 0; // 0 = down, -1 = left, 1 = right

        for (int i = 0; i < riverLength; i++) {
            if (!(terrainGrid[x][y] instanceof River)) {
                Terrain river = new River(x, y);
                gamePane.getChildren().add(river);
                terrainGrid[x][y] = river;
            }

            if (rand.nextDouble() < 0.5) {
                direction = rand.nextInt(3) - 1;
            }

            x += direction;
            y += 1;

            if (x < 1) x = 1; //not to generate on the sides (entrance and exit)
            if (x >= COLUMNS - 1) x = COLUMNS - 2;
            if (y >= ROWS) break;
        }
    }
}