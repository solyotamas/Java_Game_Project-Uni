package classes.entities.human;

import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

public class Ranger extends Human {

    private static final int frameWidth = 104;
    private static final int frameHeight = 106;
    private static final double speed = 0.8;
    private static final String imgURL = "/images/animated/ranger.png";

    public Ranger(double x, double y){
        super(x, y, frameWidth, frameHeight, imgURL, speed);

        this.setOnMouseClicked(e -> showInfoWindow(e.getX(), e.getY()));
    }

    private void showInfoWindow(double x, double y) {
        System.out.println("aa");

        Pane infoWindow = new Pane();
        infoWindow.setStyle("-fx-background-color: lightgray; -fx-border-color: black; -fx-border-width: 2px;");
        infoWindow.setPrefSize(200, 100);

        Label infoLabel = new Label("Ranger Info\nSpeed: " + this.getSpeed());
        infoWindow.getChildren().add(infoLabel);

        infoWindow.setLayoutX(x);
        infoWindow.setLayoutY(y - 100);

        Pane parent = (Pane) this.getParent();
        if (parent != null) {
            parent.getChildren().add(infoWindow);
        }

        infoWindow.setOnMouseClicked(event -> {
            if (parent != null) {
                parent.getChildren().remove(infoWindow);
            }
        });
    }
}