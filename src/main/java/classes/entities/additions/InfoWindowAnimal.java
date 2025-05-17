package classes.entities.additions;

import classes.entities.animals.Animal;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class InfoWindowAnimal extends VBox {

    public InfoWindowAnimal(Animal animal, Runnable onSellClick, Runnable onCloseClick) {
        this.getStyleClass().add("info-window");
        this.setPrefSize(180, 70);
        this.setLayoutX(animal.getLayoutX() - 85);
        this.setLayoutY(animal.getLayoutY() - 130);

        //close
        Button closeButton = new Button("×");
        closeButton.setCursor(Cursor.HAND);
        closeButton.getStyleClass().add("close-button");
        closeButton.setOnAction(e -> onCloseClick.run());

        //sell
        Button sellAnimalBtn = new Button("Sell animal");
        sellAnimalBtn.setCursor(Cursor.HAND);
        sellAnimalBtn.getStyleClass().add("info-button");
        sellAnimalBtn.setOnAction(e -> onSellClick.run());

        //info
        Text animalAge = new Text("Age: " + String.valueOf(animal.getAge()));
        animalAge.setFont(Font.font("Calibri", FontWeight.BOLD, 16));
        animalAge.setFill(Color.web("#3E2415"));
        Text lifeExpectancy = new Text("Life expectancy: " + String.valueOf(animal.getLifeExpectancy()));
        lifeExpectancy.setFont(Font.font("Calibri", FontWeight.BOLD, 16));
        lifeExpectancy.setFill(Color.web("#3E2415"));
        Text appetite = new Text("Appetite: " + String.valueOf(animal.getAppetite()));
        appetite.setFont(Font.font("Calibri", FontWeight.BOLD, 16));
        appetite.setFill(Color.web("#3E2415"));

        HBox row = new HBox(5, sellAnimalBtn, closeButton);
        row.setAlignment(Pos.CENTER);

        VBox vbox = new VBox(5, animalAge, lifeExpectancy, appetite, row);
        this.getChildren().add(vbox);


    }
}
