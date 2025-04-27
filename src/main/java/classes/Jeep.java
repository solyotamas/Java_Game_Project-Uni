package classes;

import classes.entities.Direction;
import classes.terrains.Terrain;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;

import java.util.ArrayList;

//todo:
//  - a kép útvonal még lehet rossz
//  - a többit kb a Placeableből másoltam
//  - a radiust majd ki kell találni
//  - valszeg kell majd egy ilyen irány field hogy éppen merre megyünk mert a képed majd forgatni kell


public class Jeep extends Pane {
    public double x, y;
    private double depth;
    protected ImageView imageView;


    private final double speed = 1.0;

    public int happyBonus;
    public int speciesSeen;
    private Direction currentDirection;

    //Animation
    private final Image spriteSheet = new Image(getClass().getResource("/images/jeep-spritesheet.png").toExternalForm());
    private final Image[] jeepRight = new Image[2];
    private final Image[] jeepLeft = new Image[2];
    private final Image[] jeepUp = new Image[2];
    private final Image[] jeepDown = new Image[2];


    private int currentFrame = 0;
    private int frameDelay = 0;

    //movement
    private ArrayList<Terrain> path = new ArrayList<>();
    private int pathIndex = 0;

    public Jeep(double x, double y) {
        this.x = x;
        this.y = y;
        this.depth = y + 30 / 2.;

        loadFrames();

        this.imageView = new ImageView(jeepRight[0]);
        imageView.setFitHeight(30);
        imageView.setFitWidth(30);
        getChildren().add(imageView);

        setLayoutX(x - 30 / 2.);
        setLayoutY(y - 30);
    }

    private void loadFrames() {
        // DOWN és UP: 32x47
        for (int i = 0; i < 2; i++) {
            jeepUp[i] = new WritableImage(spriteSheet.getPixelReader(), 0, i * 52, 54, 52);
            jeepDown[i] = new WritableImage(spriteSheet.getPixelReader(), 54, i * 52, 54, 52);
        }

        // LEFT és RIGHT: 67x30
        for (int i = 0; i < 2; i++) {
            jeepLeft[i] = new WritableImage(spriteSheet.getPixelReader(), 108, i * 52, 103, 52);
            jeepRight[i] = new WritableImage(spriteSheet.getPixelReader(), 211, i * 52, 103, 52);
        }
    }

    public void moveAlongPath() {
        if (pathIndex >= path.size()) {
            return;
        }

        Terrain nextTile = path.get(pathIndex);
        double targetX = nextTile.getLayoutX() + nextTile.getSize() * 0.5; //nextTile.getSize() / 2.0
        double targetY = nextTile.getLayoutY() + nextTile.getSize() * 0.5;

        double dx = targetX - this.x;
        double dy = targetY - this.y;

        // Check if close enough to the next tile
        if (Math.abs(dx) < 1 && Math.abs(dy) < 1) {
            pathIndex++;
            return;
        }

        // Normalize direction
        double dist = Math.hypot(dx, dy);
        double stepX = (dx / dist) * speed;
        double stepY = (dy / dist) * speed;

        // Determine direction
        Direction dir;
        if (Math.abs(dx) > Math.abs(dy)) {
            dir = dx > 0 ? Direction.RIGHT : Direction.LEFT;
        } else {
            dir = dy > 0 ? Direction.DOWN : Direction.UP;
        }

        move(dir, stepX, stepY);
    }
    private void move(Direction dir, double dx, double dy) {
        this.currentDirection = dir;
        this.x += dx;
        this.y += dy;

        setLayoutX(this.x - 30 / 2.0);
        setLayoutY(this.y - 30);

        // Animation update
        frameDelay++;
        if (frameDelay >= 10) {
            currentFrame = (currentFrame + 1) % 2;
            frameDelay = 0;
        }

        switch (dir) {
            case RIGHT -> imageView.setImage(jeepRight[currentFrame]);
            case LEFT  -> imageView.setImage(jeepLeft[currentFrame]);
            case UP    -> imageView.setImage(jeepUp[currentFrame]);
            case DOWN  -> imageView.setImage(jeepDown[currentFrame]);
        }
    }
    private void updateDirection(double dx, double dy) {
        Direction newDirection;

        if (Math.abs(dx) > Math.abs(dy)) {
            newDirection = dx > 0 ? Direction.RIGHT : Direction.LEFT;
        } else {
            newDirection = dy > 0 ? Direction.DOWN : Direction.UP;
        }

        if (newDirection != currentDirection) {
            currentDirection = newDirection;
            currentFrame = 0;
            frameDelay = 0;
        }
    }
    private void updateImage(Direction direction) {
        frameDelay++;
        if (frameDelay >= 10) {
            currentFrame = (currentFrame + 1) % 2;
            frameDelay = 0;
        }

        switch (direction) {
            case RIGHT -> imageView.setImage(jeepRight[currentFrame]);
            case LEFT  -> imageView.setImage(jeepLeft[currentFrame]);
            case UP    -> imageView.setImage(jeepUp[currentFrame]);
            case DOWN  -> imageView.setImage(jeepDown[currentFrame]);
        }
    }




    /*
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

            if (dx > 0) currentDirection = Direction.RIGHT;
            else if (dx < 0) currentDirection = Direction.LEFT;
            else if (dy > 0) currentDirection = Direction.DOWN;
            else if (dy < 0) currentDirection = Direction.UP;

            updateImage(currentDirection);
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
    private void updateImage(Direction direction) {
        if (this.currentDirection != direction) {
            this.currentDirection = direction;
            currentFrame = 0;
            frameDelay = 0;
        }

        frameDelay++;
        if (frameDelay >= 10) {
            currentFrame = (currentFrame + 1) % 2;
            frameDelay = 0;
        }

        switch (currentDirection) {
            case RIGHT -> picture.setImage(jeepRight[currentFrame]);
            case LEFT -> picture.setImage(jeepLeft[currentFrame]);
            case UP -> picture.setImage(jeepUp[currentFrame]);
            case DOWN -> picture.setImage(jeepDown[currentFrame]);
        }
    }
    */

    public double getDepth() {
        return this.depth;
    }
    public void setPath(ArrayList<Terrain> path) {
        this.path = path;
        this.pathIndex = 0;
    }

}