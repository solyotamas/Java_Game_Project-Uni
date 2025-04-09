package classes.entities.additions;

import classes.entities.animals.Animal;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class InfoWindow extends VBox {

    public InfoWindow(Animal animal, Runnable onSellClick) {
        this.getStyleClass().add("info-window");
        this.setPrefSize(180, 70);
        this.setLayoutX(animal.getLayoutX() - 60);
        this.setLayoutY(animal.getLayoutY() - 60);

        Button closeButton = new Button("X");
        closeButton.getStyleClass().add("close-button");

        Button sellAnimalBtn = new Button("Sell animal");
        sellAnimalBtn.getStyleClass().add("info-button");
        sellAnimalBtn.setOnAction(e -> onSellClick.run());

        HBox row = new HBox();
        row.setSpacing(5);
        row.setAlignment(Pos.CENTER);
        row.getChildren().addAll(sellAnimalBtn, closeButton);

        this.getChildren().add(row);

    }
}
