package classes.entities.human;

import classes.entities.Direction;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class Ranger extends Human {

    private static final int frameWidth = 104;
    private static final int frameHeight = 106;
    private static double speed = 0.8;
    private static final String imgURL = "/images/animated/ranger.png";

    private Pane infoWindow;
    private static Ranger currentRangerWithInfoWindow = null;

    public Ranger(double x, double y){
        super(x, y, frameWidth, frameHeight, imgURL, speed);

        this.setOnMouseClicked(e -> {
            e.consume(); //
            showInfoWindow(e.getSceneX(), e.getSceneY());
        });
    }

    private void showInfoWindow(double sceneX, double sceneY) {
        if (currentRangerWithInfoWindow != null && currentRangerWithInfoWindow != this) {
            currentRangerWithInfoWindow.closeInfoWindow();
        }

        Pane parent = (Pane) this.getParent();
        if (parent == null) return;

        closeInfoWindow();

        this.setPaused(true);

        VBox newInfoWindow = new VBox();
        newInfoWindow.getStyleClass().add("info-window");
        newInfoWindow.setPrefSize(170, 100);

        Button choosePreyButton = new Button("Choose Prey");
        choosePreyButton.getStyleClass().add("info-button");
        choosePreyButton.setOnAction(e -> choosePrey());

        Button unemployButton = new Button("Unemploy");
        unemployButton.getStyleClass().add("info-button");
        unemployButton.setOnAction(e -> unemploy());

        newInfoWindow.getChildren().addAll(choosePreyButton, unemployButton);

        newInfoWindow.setLayoutX(sceneX - 85);
        newInfoWindow.setLayoutY(sceneY - 230);

        parent.getChildren().add(newInfoWindow);
        infoWindow = newInfoWindow;

        currentRangerWithInfoWindow = this;

        parent.setOnMouseClicked(event -> {
            if (infoWindow != null && !newInfoWindow.getBoundsInParent().contains(event.getX(), event.getY())) {
                closeInfoWindow();
            }
        });
    }

    private void closeInfoWindow() {
        if (infoWindow != null) {
            ((Pane) this.getParent()).getChildren().remove(infoWindow);
            infoWindow = null;
            this.setPaused(false);
            currentRangerWithInfoWindow = null;
        }
    }

    private void choosePrey() {
        System.out.println("choosing prey for " + this.getClass());
        //TODO choose prey
    }

    private void unemploy() {
        System.out.println(this.getClass() + " unemployed");

        //TODO unemploy ranger
    }
}