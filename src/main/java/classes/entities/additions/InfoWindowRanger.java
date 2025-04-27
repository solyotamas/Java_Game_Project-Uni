package classes.entities.additions;

import classes.entities.human.Ranger;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class InfoWindowRanger extends VBox {

    public InfoWindowRanger(Ranger ranger, Runnable onUnemployClick,  Runnable onChoosePreyClick, Runnable onCloseClick) {
        this.getStyleClass().add("info-window");
        this.setPrefSize(180, 70);
        this.setLayoutX(ranger.getLayoutX() - 85);
        this.setLayoutY(ranger.getLayoutY() - 120);

        //Close
        Button closeButton = new Button("×");
        closeButton.getStyleClass().add("close-button");
        closeButton.setOnAction(e -> onCloseClick.run());

        //Choose prey
        Button choosePreyButton = new Button("Choose Prey");
        choosePreyButton.getStyleClass().add("info-button");
        choosePreyButton.setPrefWidth(140);
        choosePreyButton.setOnAction(e -> onChoosePreyClick.run());

        //Unemploy
        Button unemployButton = new Button("Unemploy");
        unemployButton.getStyleClass().add("info-button");
        unemployButton.setPrefWidth(140);
        unemployButton.setOnAction(e -> onUnemployClick.run());

        VBox leftColumn = new VBox(5, choosePreyButton, unemployButton);

        HBox row = new HBox(5, leftColumn, closeButton);
        row.setAlignment(Pos.CENTER);
        closeButton.setAlignment(Pos.CENTER);

        this.getChildren().add(row);

    }
}
