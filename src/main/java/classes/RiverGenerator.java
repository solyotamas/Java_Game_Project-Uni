package classes;

import javafx.scene.layout.Pane;

import java.util.Random;

public class RiverGenerator {
    private final int TILE_COUNT_X = 64;
    private final int TILE_COUNT_Y = 31;
    private final int TILE_SIZE = 30;
    private Random rand = new Random();

    public void generateRivers(Pane gamePane, int numberOfRivers) {
        for (int i = 0; i < numberOfRivers; i++) {
            int startX = rand.nextInt(TILE_COUNT_X);
            int startY = rand.nextInt(TILE_COUNT_Y);
            int currentX = startX;
            int currentY = startY;

            for (int j = 0; j < TILE_COUNT_Y; j++) {
                int direction = rand.nextInt(3);

                if (direction == 0) {
                    if (currentY < TILE_COUNT_Y - 1) {
                        currentY++;
                    }
                } else if (direction == 1) {
                    if (currentX < TILE_COUNT_X - 1) {
                        currentX++;
                    }
                } else if (direction == 2) {
                    if (currentX > 0) {
                        currentX--;
                    }
                }

                Tile riverTile = new River(currentX * TILE_SIZE, currentY * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                riverTile.draw(gamePane);

                if (currentY >= TILE_COUNT_Y - 1) {
                    break;
                }
            }
        }
    }
}