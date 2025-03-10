package classes.controllers;

import classes.terrains.Terrain;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Random;
import classes.terrains.*;

public class GameScreenController {
    private Stage stage;
    private static final int TILE_SIZE = 30;
    private int TILE_COUNT_X;
    private int TILE_COUNT_Y;

    @FXML
    private Pane gamePane;

    public void loadLevel() {
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        int screenWidth = (int) screenBounds.getWidth();
        int screenHeight = (int) screenBounds.getHeight();

        TILE_COUNT_X = screenWidth / TILE_SIZE;
        TILE_COUNT_Y = screenHeight / TILE_SIZE;

        Random rand = new Random();

        // Tile-ok létrehozása a játéktáblán
        for (int y = 0; y < TILE_COUNT_Y; y++) {
            for (int x = 0; x < TILE_COUNT_X; x++) {
                Terrain tile = getTileAt(x, y, rand);
                tile.draw(gamePane);
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
                Terrain hillTile = new Hill(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE);
                hillTile.draw(gamePane);
                placed++;
            }
        }
    }

    private void switchScene(ActionEvent event, String fxmlPath) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    public void switchToMain(ActionEvent event) throws IOException {
        switchScene(event, "/fxmls/main_screen.fxml");
    }

    public void switchToSave(ActionEvent event) throws IOException {
        switchScene(event, "/fxmls/save_screen.fxml");
    }
}