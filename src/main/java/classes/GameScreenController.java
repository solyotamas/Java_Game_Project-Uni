package classes;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Random;

public class GameScreenController {
    private Stage stage;
    private Scene scene;
    private final int TILE_SIZE = 30;
    private final int TILE_COUNT_X = 64;
    private final int TILE_COUNT_Y = 31;
    private int offsetX = 0;
    private int offsetY = 0;

    @FXML
    private Pane gamePane;

    public void loadLevel() {
        Random rand = new Random();
        int hillClusterCount = 0;

        for (int y = 0; y < TILE_COUNT_Y; y++) {
            for (int x = 0; x < TILE_COUNT_X; x++) {
                int tileX = (offsetX + x) * TILE_SIZE;
                int tileY = (offsetY + y) * TILE_SIZE;

                Tile tile = getTileAt(x, y, rand, hillClusterCount);

                tile.setX(tileX);
                tile.setY(tileY);
                tile.draw(gamePane);
            }
        }

        RiverGenerator riverGenerator = new RiverGenerator();
        riverGenerator.generateRivers(gamePane, 5);
    }

    private Tile getTileAt(int x, int y, Random rand, int hillClusterCount) {
        if (hillClusterCount < 5 && rand.nextInt(200) < 2) {
            addHillCluster(x, y);
            return new Hill(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
        } else {
            return new Ground(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
        }
    }

    private void addHillCluster(int startX, int startY) {
        Random rand = new Random();
        int clusterSize = rand.nextInt(3) + 1;
        int placed = 0;

        for (int y = startY - 1; y <= startY + 1 && placed < clusterSize; y++) {
            for (int x = startX - 1; x <= startX + 1 && placed < clusterSize; x++) {
                if (x >= 0 && x < TILE_COUNT_X && y >= 0 && y < TILE_COUNT_Y) {
                    Tile hillTile = new Hill(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                    hillTile.draw(gamePane);
                    placed++;
                }
            }
        }
    }

    public void switchToMain(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/fxmls/main_screen.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setMaximized(true);
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void switchToSave(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/fxmls/save_screen.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setMaximized(true);
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

}
