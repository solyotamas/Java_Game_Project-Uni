package testing;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public class GameController {

    @FXML
    private Pane gameContainer;

    @FXML
    private Pane map;
    @FXML
    private ImageView shopTree;
    @FXML
    private ImageView shopTiger;

    @FXML
    public void initialize() {
        setupShopListeners();
    }


    String imgPath = "";

    // ✅ Correct method to add plants to the map at a specific location
    private void addPlant(String imagePath, double x, double y) {
        ImageView plant = new ImageView(new Image(getClass().getResourceAsStream(imagePath)));
        plant.setFitWidth(50);
        plant.setFitHeight(50);
        plant.setLayoutX(x);
        plant.setLayoutY(y);

        map.getChildren().add(plant); // ✅ Add the plant to the map pane
    }

    // ✅ Correct way to allow placing plants by clicking the map
    public void enablePlantPlacement(String imagePath) {
        map.setOnMouseClicked(event -> { // ✅ Now using map instead of `this`
            double clickX = event.getX() - 25; // Centering the plant
            double clickY = event.getY() - 25;

            addPlant(imgPath, clickX, clickY);
        });
    }

    // ✅ Setting up shop item selection (so clicking a shop item allows plant placement)
    private void setupShopListeners() {
        shopTree.setOnMouseClicked(event -> {
            enablePlantPlacement("/images/tree.png"); // Clicking tree selects it for placement
            imgPath = "/images/tree.png";
            System.out.println(getClass().getResource(imgPath));
        });

        shopTiger.setOnMouseClicked(event -> {
            enablePlantPlacement("/images/tiger.png"); // Clicking tiger selects it for placement
            imgPath = "/images/loos.png";
            System.out.println(getClass().getResource(imgPath));
        });
    }


}
