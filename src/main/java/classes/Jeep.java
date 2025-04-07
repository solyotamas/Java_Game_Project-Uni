package classes;

import classes.entities.Direction;
import classes.landforms.Road;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import java.util.ArrayList;

//todo:
//  - a kép útvonal még lehet rossz
//  - a többit kb a Placeableből másoltam
//  - a radiust majd ki kell találni
//  - valszeg kell majd egy ilyen irány field hogy éppen merre megyünk mert a képed majd forgatni kell


//mire kell az absrtact?
public class Jeep extends Pane {
    public double x, y;
    protected ImageView picture;
    private int frameWidth;
    private int frameHeight;
    public int radius;
    public int happyBonus;
    public int speciesSeen;
    private Direction currentDirection;

    public Jeep(int x, int y) {
        this.x = x;
        this.y = y;
        this.frameWidth = 57;
        this.frameHeight = 30;
        this.radius = 0; //todo
        this.happyBonus = 0;
        this.speciesSeen = 0;
        this.currentDirection = Direction.RIGHT;

        Image img = new Image(getClass().getResource("/images/jeep.png").toExternalForm());
        this.picture = new ImageView(img);
        this.picture.setFitWidth(frameWidth);
        this.picture.setFitHeight(frameHeight);
        this.getChildren().add(picture);

        setLayoutX(x);
        setLayoutY(y);
    }

    public void autoMove(ArrayList<Road> roads) {
        double step = 1;

        if (tryMove(step, 0, roads)) return;      // right
        if (tryMove(0, step, roads)) return;      // down
        if (tryMove(-step, 0, roads)) return;     // left
        if (tryMove(0, -step, roads)) return;     // up
    }

    public boolean tryMove(double dx, double dy, ArrayList<Road> roads) {
        double newX = x + dx;
        double newY = y + dy;

        if (isOnRoad(newX, newY, roads)) {
            x = newX;
            y = newY;
            setLayoutX(x);
            setLayoutY(y);
            //updateImage(currentDirection);
            return true;
        }

        return false;
    }

    private boolean isOnRoad(double newX, double newY, ArrayList<Road> roads) {
        double[][] corners = {
                { newX, newY },                     // top-left
                { newX + frameWidth, newY },             // top-right
                { newX, newY + frameHeight },            // bottom-left
                { newX + frameWidth, newY + frameHeight }     // bottom-right
        };

        for (double[] corner : corners) {
            boolean onAnyRoad = false;
            for (Road road : roads) {
                if (road.getBoundsInParent().contains(corner[0], corner[1])) {
                    onAnyRoad = true;
                    break;
                }
            }
            if (!onAnyRoad) return false;
        }

        return true;
    }

/*    private void updateImage(Direction direction) {
        // Csak akkor változtatunk képet, ha az irány változott
        if (this.currentDirection != direction) {
            this.currentDirection = direction;

            // Eltávolítjuk az előző képet
            this.getChildren().remove(picture);

            // Kép frissítése az irány szerint
            Image img = null;
            switch (direction) {
                case RIGHT:
                    img = new Image(getClass().getResource("/images/jeep.png").toExternalForm());
                    this.frameWidth = 57; // Módosítható méret
                    this.frameHeight = 30;
                    break;
                case DOWN:
                    img = new Image(getClass().getResource("/images/jeep.png").toExternalForm());
                    this.frameWidth = 30;
                    this.frameHeight = 30;
                    break;
                case LEFT:
                    img = new Image(getClass().getResource("/images/jeep.png").toExternalForm());
                    this.frameWidth = 57;
                    this.frameHeight = 30;
                    break;
                case UP:
                    img = new Image(getClass().getResource("/images/jeep.png").toExternalForm());
                    this.frameWidth = 30;
                    this.frameHeight = 30;
                    break;
            }

            // Beállítjuk az új képet és méretet
            picture = new ImageView(img);
            picture.setFitWidth(frameWidth);
            picture.setFitHeight(frameHeight);
            this.getChildren().add(picture);
        }
    }*/
}