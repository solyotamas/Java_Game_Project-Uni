package classes.entities.additions;

import classes.entities.animals.Animal;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

public class InfoWindow extends VBox {

    public InfoWindow(Animal animal, Runnable onSellClick) {
        this.getStyleClass().add("info-window");
        this.setPrefSize(180, 70);
        this.setLayoutX(animal.getLayoutX() - 60);
        this.setLayoutY(animal.getLayoutY() - 60);

        Button sellAnimalBtn = new Button("Sell animal");
        sellAnimalBtn.getStyleClass().add("info-button");
        sellAnimalBtn.setOnAction(e -> onSellClick.run());

        this.getChildren().add(sellAnimalBtn);



    }
}
