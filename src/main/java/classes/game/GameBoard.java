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
    //representation
    private final Pane gamePane;
    private final Terrain[][] terrainGrid = new Terrain[COLUMNS][ROWS];
    //conf
    private final Random rand = new Random();
    private final boolean[][] isTileOccupied = new boolean[COLUMNS][ROWS];


    public GameBoard(Pane gamePane) {
        this.gamePane = gamePane;

    }

    public void setupBoard() {

        for (int x = 0; x < COLUMNS; x++) {
            for (int y = 0; y < ROWS; y++) {
                if(!isTileOccupied[x][y]){
                    makeTerrain(x, y);
                }

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
        int clusterSize = rand.nextInt(3) + 1;
        int placed = 0;

        for (int dy = -1; dy <= 1; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                int x = startX + dx;
                int y = startY + dy;

                if (x >= 0 && x < COLUMNS && y >= 0 && y < ROWS && !isTileOccupied[x][y]) {
                    Terrain hill = new Hill(x, y);
                    gamePane.getChildren().add(hill);
                    terrainGrid[x][y] = hill;
                    isTileOccupied[x][y] = true;
                    placed++;
                    System.out.println("Placing hill at: " + x + ", " + y);
                    System.out.println("Cluster size: " + clusterSize + ", Placed so far: " + placed);
                    if (placed >= clusterSize) return;
                }
            }
        }

    }



}