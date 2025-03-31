package classes.game;

import classes.landforms.Landform;
import classes.landforms.Road;
import classes.landforms.plants.Bush;
import classes.landforms.plants.Grass;
import classes.landforms.plants.Plant;
import classes.landforms.plants.Tree;
import classes.terrains.*;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;


import java.util.ArrayList;
import java.util.Random;

public class GameBoard{
    //stats
    private static final int ROWS = 31;
    private static final int COLUMNS = 64;
    private static final int TILE_SIZE = 30;

    //representation
    private final Pane terrainLayer;
    private final Pane uiLayer;

    private final Pane shopPane;
    private final Button marketButton;
    private final Terrain[][] terrainGrid = new Terrain[COLUMNS][ROWS];

    //conf
    private final Random rand = new Random();

    public GameBoard( Pane terrainLayer, Pane uiLayer, Pane shopPane, Button marketButton) {
        this.terrainLayer = terrainLayer;
        this.uiLayer = uiLayer;

        this.shopPane = shopPane;
        this.marketButton = marketButton;

    }

    public void setupGroundBoard(){
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
            generateRiver(rand.nextInt(49) + 8, 0);
        }

    }

    //terrain generating
    private void makeFenceTerrain(int x, int y){
        Terrain fence = new Fence(x, y);

        if (x == COLUMNS - 5) {
            fence.setScaleX(-1);
        }

        terrainLayer.getChildren().add(fence);
        terrainGrid[x][y] = fence;
    }

    private void makeFloorTerrain(int x, int y){
        Terrain floor = new Floor(x,y);

        terrainLayer.getChildren().add(floor);
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
        terrainLayer.getChildren().add(terrain);
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
                terrainLayer.getChildren().add(river);
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
                terrainLayer.getChildren().add(hillTerrain);
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

    //Placing landforms
    public boolean canPlaceLandform(Landform landform, int startX, int startY) {
        for (int x = startX; x < startX + landform.getWidthInTiles(); x++) {
            for (int y = startY; y < startY + landform.getHeightInTiles(); y++) {
                Terrain terrain = getTerrainAt(x, y);
                if (terrain == null || terrain.hasLandform() || terrain instanceof Hill ||
                        terrain instanceof River || terrain instanceof Fence || terrain instanceof Floor) {
                    return false;
                }
            }
        }
        return true;
    }

    public void placeLandform(Landform landform, int x, int y) {
        for (int i = x; i < x + landform.getWidthInTiles(); i++) {
            for (int j = y; j < y + landform.getHeightInTiles(); j++) {
                Terrain terrain = getTerrainAt(i, j);
                if (terrain != null) {
                    terrain.placeLandform(landform);
                    if(landform instanceof Road){
                        updateRoadAndNeighbors(i, j);
                    }
                }
            }
        }
    }

    //TODO simplify createPlant and generatePlants into one, idk how tho
    private void createPlant(Class<? extends Plant> plantClass, int x, int y) {
        Image plantImage = switch (plantClass.getSimpleName()) {
            case "Tree" -> Tree.getRandomTreeImage();
            case "Bush" -> Bush.getRandomBushImage();
            case "Grass" -> Grass.grassPicture;
            default -> null;
        };

        Landform tempInstance = null;
        try {
            tempInstance = plantClass.getDeclaredConstructor(double.class, double.class, double.class, Image.class).newInstance(0.0, 0.0, 0.0, plantImage);
        } catch (Exception e) {
            e.printStackTrace();
        }
        double depth = y * TILE_SIZE + tempInstance.getHeightInTiles() * TILE_SIZE;

        try {
            Landform placedLandform = plantClass
                    .getDeclaredConstructor(double.class, double.class, double.class, Image.class)
                    .newInstance(x * TILE_SIZE, y * TILE_SIZE, depth, plantImage);

            if (canPlaceLandform(placedLandform, x, y)) {
                placeLandform(placedLandform, x, y);
                uiLayer.getChildren().add(placedLandform);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void generatePlants(int amount) {
        for (int i = 0; i < amount; i++) {
            int x, y;
            Class<? extends Plant> plantClass = switch (rand.nextInt(3)) {
                case 0 -> Tree.class;
                case 1 -> Bush.class;
                default -> Grass.class;
            };

            Image plantImage = switch (plantClass.getSimpleName()) {
                case "Tree" -> Tree.getRandomTreeImage();
                case "Bush" -> Bush.getRandomBushImage();
                case "Grass" -> Grass.grassPicture;
                default -> null;
            };

            if (plantImage == null) continue;

            Landform tempInstance = null;
            try {
                tempInstance = plantClass.getDeclaredConstructor(double.class, double.class, double.class, Image.class)
                        .newInstance(0.0, 0.0, 0.0, plantImage);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }

            do {
                x = rand.nextInt(COLUMNS);
                y = rand.nextInt(ROWS);
            } while (!canPlaceLandform(tempInstance, x, y));

            createPlant(plantClass, x, y);
        }
    }

    //road updates
    private void updateRoadAndNeighbors(int x, int y) {
        updateRoadTextureAt(x, y);
        updateRoadTextureAt(x + 1, y);
        updateRoadTextureAt(x - 1, y);
        updateRoadTextureAt(x, y + 1);
        updateRoadTextureAt(x, y - 1);
    }

    private void updateRoadTextureAt(int x, int y) {
        Terrain terrain = getTerrainAt(x, y);
        if (terrain != null && terrain.hasLandform() && terrain.getLandform() instanceof Road road) {
            int bitmask = calculateBitmask(x, y);
            road.setPicture(Road.roadImages[bitmask]);
        }
    }

    private int calculateBitmask(int x, int y) {
        int bitmask = 0;

        if (isRoadAt(x, y - 1)) { // Top
            bitmask += 1;
        }
        if (isRoadAt(x + 1, y)) { // Right
            bitmask += 2;
        }
        if (isRoadAt(x, y + 1)) { // Bottom
            bitmask += 4;
        }
        if (isRoadAt(x - 1, y)) { // Left
            bitmask += 8;
        }

        return bitmask;
    }
    private boolean isRoadAt(int gridX, int gridY) {
        Terrain terrain = getTerrainAt(gridX, gridY);
        return terrain != null && terrain.hasLandform() && terrain.getLandform() instanceof Road;
    }

    //getters, setters
    public Pane getUiLayer() { return this.uiLayer; }
}