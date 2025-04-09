package classes.entities.additions;

import classes.entities.animals.Animal;
import classes.entities.human.Human;
import classes.entities.human.Ranger;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class InfoWindowRanger extends VBox {

    public InfoWindowRanger(Ranger ranger, Runnable onUnemployClick, Runnable onCloseClick, Runnable onChoosePreyClick) {
        this.getStyleClass().add("info-window");
        this.setPrefSize(180, 70);
        this.setLayoutX(ranger.getLayoutX() - 60);
        this.setLayoutY(ranger.getLayoutY() - 60);

        //Close
        Button closeButton = new Button("×");
        closeButton.getStyleClass().add("close-button");
        closeButton.setOnAction(e -> onCloseClick.run());

        //Choose prey
        Button choosePreyButton = new Button("Choose Prey");
        choosePreyButton.getStyleClass().add("info-button");
        choosePreyButton.setOnAction(e -> onChoosePreyClick.run());

        //Unemploy
        Button unemployButton = new Button("Unemploy");
        unemployButton.getStyleClass().add("info-button");
        unemployButton.setOnAction(e -> onUnemployClick.run());



        //edit
        HBox row = new HBox(5, choosePreyButton, unemployButton, closeButton);
        row.setAlignment(Pos.CENTER);
        this.getChildren().add(row);


    }
}
