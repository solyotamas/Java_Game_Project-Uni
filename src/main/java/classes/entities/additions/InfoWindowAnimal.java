package classes.entities.additions;

import classes.entities.animals.Animal;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class InfoWindowAnimal extends VBox {

    public InfoWindowAnimal(Animal animal, Runnable onSellClick, Runnable onCloseClick) {
        this.getStyleClass().add("info-window");
        this.setPrefSize(180, 70);
        this.setLayoutX(animal.getLayoutX() - 60);
        this.setLayoutY(animal.getLayoutY() - 60);

        //close
        Button closeButton = new Button("×");
        closeButton.getStyleClass().add("close-button");
        closeButton.setOnAction(e -> onCloseClick.run());

        //sell
        Button sellAnimalBtn = new Button("Sell animal");
        sellAnimalBtn.getStyleClass().add("info-button");
        sellAnimalBtn.setOnAction(e -> onSellClick.run());


        HBox row = new HBox(5, sellAnimalBtn, closeButton);
        row.setAlignment(Pos.CENTER);
        this.getChildren().add(row);


    }
}
