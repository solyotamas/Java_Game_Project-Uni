package classes;

import classes.entities.Direction;
import classes.entities.animals.Animal;
import classes.terrains.Terrain;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

//todo:
//  - a kép útvonal még lehet rossz
//  - a többit kb a Placeableből másoltam
//  - a radiust majd ki kell találni
//  - valszeg kell majd egy ilyen irány field hogy éppen merre megyünk mert a képed majd forgatni kell


public class Jeep extends Pane {
    public double x, y;
    private double depth;
    protected ImageView imageView;


    private final double speed = 2.0;

    //public int speciesSeen;
    private Set<Class<? extends Animal>> speciesSeen = new HashSet<>();
    private Direction currentDirection;
    private JeepState state;

    //Animation
    private final Image spriteSheet = new Image(getClass().getResource("/images/jeep-spritesheet.png").toExternalForm());
    private Image jeepRight;
    private Image jeepLeft;
    private Image jeepUp;
    private Image jeepDown;


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

        this.imageView = new ImageView(jeepRight);
        imageView.setFitHeight(30);
        imageView.setFitWidth(30);
        getChildren().add(imageView);


        setLayoutX(x - 30 / 2.);
        setLayoutY(y - 15);
    }

    private void loadFrames() {
        jeepUp = new WritableImage(spriteSheet.getPixelReader(), 0, 0, 50, 50);
        jeepDown = new WritableImage(spriteSheet.getPixelReader(), 50, 0, 50, 50);
        jeepLeft = new WritableImage(spriteSheet.getPixelReader(), 100, 0, 50, 50);
        jeepRight = new WritableImage(spriteSheet.getPixelReader(), 150, 0, 50, 50);
    }

    public void moveAlongPath() {
        if (pathIndex >= path.size()) {
            state = JeepState.IDLE;

            System.out.println("Jeep saw these species:");
            for (Class<? extends Animal> species : speciesSeen) {
                System.out.println("- " + species.getSimpleName());
            }
            speciesSeen.clear();

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
        setLayoutY(this.y - 15);

        // Animation update
        frameDelay++;
        if (frameDelay >= 10) {
            currentFrame = (currentFrame + 1) % 2;
            frameDelay = 0;
        }

        switch (dir) {
            case RIGHT -> imageView.setImage(jeepRight);
            case LEFT  -> imageView.setImage(jeepLeft);
            case UP    -> imageView.setImage(jeepUp);
            case DOWN  -> imageView.setImage(jeepDown);
        }
    }

    public double getDepth() {
        return this.depth;
    }
    public void setPath(ArrayList<Terrain> path) {
        this.path = path;
        this.pathIndex = 0;
    }

    public Image getJeepLeft(){
        return this.jeepLeft;
    }
    public void setImageView(Image img){
        this.imageView.setImage(img);
    }
    public void transitionTo(JeepState newState){
        this.state = newState;
    }

    public JeepState getStatus() {
        return this.state;
    }

    public double getX(){
        return this.x;
    }
    public double getY(){
        return this.y;
    }

    public Set<Class<? extends Animal>> getSpeciesSeen() {
        return speciesSeen;
    }
}