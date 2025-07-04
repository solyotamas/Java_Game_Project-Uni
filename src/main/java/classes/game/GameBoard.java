package classes.game;

import classes.landforms.Lake;
import classes.landforms.Landform;
import classes.landforms.Road;
import classes.landforms.plants.Bush;
import classes.landforms.plants.Grass;
import classes.landforms.plants.Plant;
import classes.landforms.plants.Tree;
import classes.terrains.*;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;


import java.util.*;

public class GameBoard {
    //stats
    private static final int ROWS = 31;
    private static final int COLUMNS = 64;
    private static final int TILE_SIZE = 30;

    //representation
    private final Pane terrainLayer;
    private final Pane uiLayer;


    private final Terrain[][] terrainGrid = new Terrain[COLUMNS][ROWS];

    //conf
    private final Random rand = new Random();

    public GameBoard(Pane terrainLayer, Pane uiLayer) {
        this.terrainLayer = terrainLayer;
        this.uiLayer = uiLayer;
    }

    public void setupGroundBoard() {
        //Map config at start
        for (int x = 0; x < COLUMNS; x++) {
            for (int y = 0; y < ROWS; y++) {
                switch (x) {
                    case 0, 1, 2, 3, COLUMNS - 1, COLUMNS - 2, COLUMNS - 3, COLUMNS - 4:
                        makeFloorTerrain(x, y);
                        break;
                    case 4, COLUMNS - 5:
                        if (y == 0 && x == 4 || y == ROWS - 1 && x == COLUMNS - 5) {
                            makeEntranceTerrain(x, y);
                        } else {
                            makeFenceTerrain(x, y);
                        }
                        break;
                    default:
                        makeRandomMapTerrain(x, y);
                }
            }
        }

        Random rand = new Random();
        for (int i = 0; i < 3; i++) {
            generateRiver(rand.nextInt(49) + 8, 0);
        }

    }

    //terrain generating
    private void makeFenceTerrain(int x, int y) {
        Terrain fence = new Fence(x, y);

        if (x == COLUMNS - 5) {
            fence.setScaleX(-1);
        }

        terrainLayer.getChildren().add(fence);
        terrainGrid[x][y] = fence;
    }

    private void makeEntranceTerrain(int x, int y) {
        Terrain entrance = new Entrance(x, y);

        if (x == COLUMNS - 5) {
            entrance.setScaleX(-1);
        }

        terrainLayer.getChildren().add(entrance);
        terrainGrid[x][y] = entrance;
    }

    private void makeFloorTerrain(int x, int y) {
        Terrain floor = new Floor(x, y);

        terrainLayer.getChildren().add(floor);
        terrainGrid[x][y] = floor;
    }

    private void makeRandomMapTerrain(int x, int y) {
        int terrainType = rand.nextInt(500);

        Terrain terrain;

        if (terrainType < 5) {
            if ((x < 11 && y < 5) || (x > COLUMNS - 12 && y > ROWS - 6)) {
                terrain = new Ground(x, y);
            } else {
                terrain = new Hill(x, y);
                addHillCluster(x, y);
            }
        } else {
            terrain = new Ground(x, y);
        }

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
            if (terrain instanceof Hill || terrain instanceof Fence || terrain instanceof Entrance) {
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
        } else return null;
    }

    public Terrain getTerrainAtDouble(double x, double y) {
        int tileX = (int) (x / TILE_SIZE);
        int tileY = (int) (y / TILE_SIZE);
        Terrain terrain = getTerrainAt(tileX, tileY);

        return terrain;
    }

    //Placing landforms
    public boolean canPlaceLandform(Landform landform, int startX, int startY) {
        for (int x = startX; x < startX + landform.getWidthInTiles(); x++) {
            for (int y = startY; y < startY + landform.getHeightInTiles(); y++) {
                Terrain terrain = getTerrainAt(x, y);
                if (terrain == null || terrain.hasLandform() || terrain instanceof Hill || terrain instanceof Fence || terrain instanceof Floor || terrain instanceof Entrance || (!(landform instanceof Road) && terrain instanceof River)) {
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

                    if (terrain instanceof River && landform instanceof Road) {
                        ((Road) landform).setAsBridge();
                    }

                    if (landform instanceof Road) {
                        updateRoadAndNeighbors(i, j);
                    }
                }
            }
        }
    }

    //Movement for animals
    public ArrayList<Terrain> getPlantTerrains() {
        ArrayList<Terrain> plantTiles = new ArrayList<>();

        for (int x = 0; x < terrainGrid.length; x++) {
            for (int y = 0; y < terrainGrid[0].length; y++) {
                Terrain terrain = terrainGrid[x][y];
                if (terrain != null && terrain.hasLandform() && terrain.getLandform() instanceof Plant) {
                    plantTiles.add(terrain);
                }
            }
        }

        return plantTiles;
    }

    public ArrayList<Terrain> getLakeTerrains() {
        ArrayList<Terrain> lakeTiles = new ArrayList<>();

        for (int x = 0; x < terrainGrid.length; x++) {
            for (int y = 0; y < terrainGrid[0].length; y++) {
                Terrain terrain = terrainGrid[x][y];
                if (terrain != null && ((terrain.hasLandform() && terrain.getLandform() instanceof Lake) || terrain instanceof River)) {
                    lakeTiles.add(terrain);
                }
            }
        }

        return lakeTiles;
    }

    public ArrayList<Terrain> getGroundTerrains() {
        ArrayList<Terrain> groundTiles = new ArrayList<>();

        for (int x = 0; x < terrainGrid.length; x++) {
            for (int y = 0; y < terrainGrid[0].length; y++) {
                Terrain terrain = terrainGrid[x][y];
                if (terrain != null && !terrain.hasLandform() && terrain instanceof Ground) {
                    groundTiles.add(terrain);
                }
            }
        }

        return groundTiles;
    }

    public ArrayList<Terrain> getNeighbors(Terrain tile) {
        ArrayList<Terrain> neighbors = new ArrayList<>();

        int x = tile.getRow();
        int y = tile.getCol();

        int[][] directions = {{0, -1},  // Up
                {0, 1},   // Down
                {-1, 0},  // Left
                {1, 0}    // Right
        };

        for (int[] dir : directions) {
            int nx = x + dir[0];
            int ny = y + dir[1];

            Terrain neighbor = getTerrainAt(nx, ny);
            if (neighbor != null && neighbor.isWalkable()) {
                neighbors.add(neighbor);
            }
        }

        return neighbors;
    }

    public ArrayList<Terrain> findPathDijkstra(Terrain start, Terrain goal) {
        if (start == null || goal == null) {
            return new ArrayList<>();
        }

        //System.out.println("Starting Dijkstra from: " + start + " to " + goal);

        Map<Terrain, Terrain> cameFrom = new HashMap<>();
        Map<Terrain, Integer> costSoFar = new HashMap<>();
        PriorityQueue<Terrain> frontier = new PriorityQueue<>(Comparator.comparingInt(costSoFar::get));

        costSoFar.put(start, 0);
        frontier.add(start);

        while (!frontier.isEmpty()) {
            Terrain current = frontier.poll();

            if (current == goal) {
                break;
            }

            for (Terrain neighbor : getNeighbors(current)) {
                int newCost = costSoFar.get(current) + current.getCrossingDifficulty() + neighbor.getCrossingDifficulty();

                if (!costSoFar.containsKey(neighbor) || newCost < costSoFar.get(neighbor)) {
                    costSoFar.put(neighbor, newCost);
                    cameFrom.put(neighbor, current);
                    frontier.add(neighbor);
                }
            }
        }

        // Reconstruct the path
        ArrayList<Terrain> path = new ArrayList<>();
        Terrain current = goal;

        while (current != null && cameFrom.containsKey(current)) {
            path.add(0, current); // insert at beginning
            current = cameFrom.get(current);
        }

        // If the path doesn't start at 'start', we probably never reached goal
        if (!path.isEmpty() && path.get(0) != start) {
            path.add(0, start);
        }

        if (path.isEmpty()) {
            //System.out.println("no path found from " + start + " to " + goal);
        } else {
            //System.out.println("path found, length: " + path.size());
        }

        return path;
    }

    //Movement for jeeps
    public ArrayList<Terrain> findRoadPathDijkstra(Terrain start, Terrain goal) {
        if (start == null || goal == null) return new ArrayList<>();

        Map<Terrain, Terrain> cameFrom = new HashMap<>();
        Map<Terrain, Integer> costSoFar = new HashMap<>();
        PriorityQueue<Terrain> frontier = new PriorityQueue<>(Comparator.comparingInt(costSoFar::get));

        costSoFar.put(start, 0);
        frontier.add(start);

        while (!frontier.isEmpty()) {
            Terrain current = frontier.poll();

            if (current == goal) break;

            for (Terrain neighbor : getRoadNeighbors(current)) {
                int newCost = costSoFar.get(current) + 1; // road cost = 1

                if (!costSoFar.containsKey(neighbor) || newCost < costSoFar.get(neighbor)) {
                    costSoFar.put(neighbor, newCost);
                    cameFrom.put(neighbor, current);
                    frontier.add(neighbor);
                }
            }
        }

        // Reconstruct path
        ArrayList<Terrain> path = new ArrayList<>();
        Terrain current = goal;

        while (current != null && cameFrom.containsKey(current)) {
            path.add(0, current);
            current = cameFrom.get(current);
        }

        if (!path.isEmpty() && path.get(0) != start) path.add(0, start);

        return path;
    }

    public ArrayList<Terrain> getRoadNeighbors(Terrain tile) {
        ArrayList<Terrain> neighbors = new ArrayList<>();

        int x = tile.getRow();
        int y = tile.getCol();

        int[][] directions = {{0, -1}, {0, 1}, {-1, 0}, {1, 0}};

        for (int[] dir : directions) {
            int nx = x + dir[0];
            int ny = y + dir[1];
            Terrain neighbor = getTerrainAt(nx, ny);
            if (neighbor != null && neighbor.hasLandform() && neighbor.getLandform() instanceof Road) {
                neighbors.add(neighbor);
            }
        }

        return neighbors;
    }

    public ArrayList<Terrain> getRoadTerrains() {
        ArrayList<Terrain> roadTiles = new ArrayList<>();
        for (int x = 0; x < terrainGrid.length; x++) {
            for (int y = 0; y < terrainGrid[0].length; y++) {
                Terrain terrain = terrainGrid[x][y];
                if (terrain != null && terrain.hasLandform() && terrain.getLandform() instanceof Road) {
                    roadTiles.add(terrain);
                }
            }
        }
        return roadTiles;
    }

    // Generating plants when new game
    private void createPlant(Class<? extends Plant> plantClass, int x, int y) {
        if ((x < 11 && y < 5) || (x > COLUMNS - 12 && y > ROWS - 6)) return;

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
            Landform placedLandform = plantClass.getDeclaredConstructor(double.class, double.class, double.class, Image.class).newInstance(x * TILE_SIZE, y * TILE_SIZE, depth, plantImage);

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
                tempInstance = plantClass.getDeclaredConstructor(double.class, double.class, double.class, Image.class).newInstance(0.0, 0.0, 0.0, plantImage);
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

    // Road updates
    private void updateRoadAndNeighbors(int x, int y) {
        updateRoadTextureAt(x, y);
        updateRoadTextureAt(x + 1, y);
        updateRoadTextureAt(x - 1, y);
        updateRoadTextureAt(x, y + 1);
        updateRoadTextureAt(x, y - 1);
    }

    private void updateRoadTextureAt(int x, int y) {
        Terrain terrain = getTerrainAt(x, y);
        if (terrain != null && terrain.hasLandform() && terrain.getLandform() instanceof Road road && !road.isBridge()) {
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
    public Pane getUiLayer() {
        return this.uiLayer;
    }

    public Terrain[][] getTerrainGrid() {
        return terrainGrid;
    }
}