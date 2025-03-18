package classes.game;

import classes.terrains.*;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;


import java.util.Random;

public class GameBoard{
    //stats
    private static final int ROWS = 31;
    private static final int COLUMNS = 64;
    //representation
    private final Pane gamePane;
    private final Terrain[][] terrainGrid = new Terrain[COLUMNS][ROWS];

    //market
    private Button marketUI;

    //conf
    private final Random rand = new Random();


    public GameBoard(Pane gamePane, Button valami) {
        this.gamePane = gamePane;
        this.marketUI = valami;
    }

    public void setupBoard() {

        for (int x = 0; x < COLUMNS; x++) {
            for (int y = 0; y < ROWS; y++) {
                switch (x){
                    case 0,1,2,3, COLUMNS-1, COLUMNS-2, COLUMNS-3, COLUMNS-4:
                        makeFloorTerrain(x,y);
                        break;
                    case 4, COLUMNS-5:
                        makeFenceTerrain(x,y);
                        break;
                    default:
                        makeRandomMapTerrain(x,y);
                }
            }

        }
        Market market = new Market(marketUI);
    }

    private void makeFenceTerrain(int x, int y){
        Terrain fence = new Fence(x,y);

        gamePane.getChildren().add(fence);
        terrainGrid[x][y] = fence;
    }

    private void makeFloorTerrain(int x, int y){
        Terrain floor = new Floor(x,y);

        gamePane.getChildren().add(floor);
        terrainGrid[x][y] = floor;
    }

    private void makeRandomMapTerrain(int x, int y) {
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