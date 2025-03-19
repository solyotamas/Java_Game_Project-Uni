package classes.game;

import classes.placeables.Plant;
import classes.terrains.*;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;


import java.util.ArrayList;
import java.util.Random;

public class GameBoard{
    //stats
    private static final int ROWS = 31;
    private static final int COLUMNS = 64;


    //representation
    private final Pane gamePane;
    private final Terrain[][] terrainGrid = new Terrain[COLUMNS][ROWS];

    //market
    private Button marketButton;
    private Pane shopPane;


    //conf
    private final Random rand = new Random();


    public GameBoard(Pane gamePane, Pane shopPane, Button marketButton) {
        this.gamePane = gamePane;
        this.shopPane = shopPane;
        this.marketButton = marketButton;
    }

    public void setupBoard() {

        //Map config at start
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

        Random rand = new Random();
        for (int i = 0; i < 3; i++) {
            generateRiver(rand.nextInt(COLUMNS - 10) + 6, 0);
        }

        //Shop config at start
        shopPane.setVisible(false);
        //shopPane.setManaged(false);
        //shopPane.toFront();

        //Market config at start
        marketButton.toFront();

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
        int terrainType = rand.nextInt(500);

        Terrain terrain;
        if (terrainType < 5){
            terrain = new Hill(x, y);
            addHillCluster(x, y);
        }
        else
            terrain = new Ground(x, y);

        //Placing inside gamePane and keeping track inside the matrix
        gamePane.getChildren().add(terrain);
        terrainGrid[x][y] = terrain;
    }

    private void generateRiver(int startX, int startY) {
        int x = startX;
        int y = startY;

        int riverLength = rand.nextInt(15) + 20;
        int direction = 0; // 0 = down, -1 = left, 1 = right

        for (int i = 0; i < riverLength; i++) {
            Terrain terrain = getTerrainAt(x, y);

            if (terrain != null) {
                Terrain river = new River(x, y);
                gamePane.getChildren().add(river);
                terrainGrid[x][y] = river;
            }

            if (rand.nextDouble() < 0.5) {
                direction = rand.nextInt(3) - 1;
            }

            x += direction;
            y += 1;

            if (x < 1) x = 1;
            if (x >= COLUMNS - 5) x = COLUMNS - 5;
            if (y >= ROWS) break;

            terrain = getTerrainAt(x, y);
            if (terrain instanceof Hill || terrain instanceof Fence) {
                x -= direction;
            }
        }
    }

    private void addHillCluster(int startX, int startY) {
        int clusterSize = rand.nextInt(20) + 5;
        ArrayList<int[]> positions = new ArrayList<>();
        positions.add(new int[]{startX, startY});

        int placed = 0;
        while (placed < clusterSize && !positions.isEmpty()) {
            int[] pos = positions.remove(rand.nextInt(positions.size()));

            int x = pos[0];
            int y = pos[1];

            if ((x > 4 && x < COLUMNS - 5) && !(terrainGrid[x][y] instanceof Hill)) {
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

    public Terrain getTerrainAt(int x, int y) {
        if (x >= 0 && y >= 0 && x < COLUMNS && y < ROWS) {
            return terrainGrid[x][y];
        }else
            return null;

    }
    public boolean canPlaceTree(int startX, int startY) {
        for (int x = startX; x < startX + 2; x++) {
            for (int y = startY; y < startY + 2; y++) {
                Terrain terrain = getTerrainAt(x, y);
                if (terrain == null || terrain.hasPlant() || terrain instanceof Hill || terrain instanceof River || terrain instanceof Fence || terrain instanceof Floor) {
                    return false;
                }
            }
        }
        return true;
    }
    public boolean canPlaceLake(int startX, int startY) {
        for (int x = startX; x < startX + 4; x++) {
            for (int y = startY; y < startY + 2; y++) {
                Terrain terrain = getTerrainAt(x, y);
                if (terrain == null || terrain.hasPlant() || terrain instanceof Hill || terrain instanceof River  || terrain instanceof Fence || terrain instanceof Floor) {
                    return false;
                }
            }
        }
        return true;
    }
    public boolean canPlaceBush(int x, int y){
        Terrain terrain = getTerrainAt(x, y);
        if (terrain == null || terrain.hasPlant() || terrain instanceof Hill || terrain instanceof River  || terrain instanceof Fence || terrain instanceof Floor) {
            return false;
        }
        return true;
    }


    public void placeMultiTilePlant(Plant plant, int x, int y, int widthInTiles, int heightInTiles) {

        //Placing the plane in the global gamePane but still covering entirely the 2 panes underneath
        plant.setLayoutX(x * 30);
        plant.setLayoutY(y * 30);
        gamePane.getChildren().add(plant);

        //marking the 2 panes underneath it as occupied by the plant
        for (int i = x; i < x + widthInTiles; i++) {
            for (int j = y; j < y + heightInTiles; j++) {
                Terrain terrain = getTerrainAt(i, j);
                if (terrain != null) {
                    terrain.placePlant(plant);
                }
            }
        }
    }
    public void placeSingleTilePlant(Plant plant, int x, int y){
        plant.setLayoutX(x * 30);
        plant.setLayoutY(y * 30);
        gamePane.getChildren().add(plant);

        Terrain terrain = getTerrainAt(x,y);
        terrain.placePlant(plant);
    }


    public int getColumns(){
        return COLUMNS;
    }
    public int getRows(){
        return ROWS;
    }


}