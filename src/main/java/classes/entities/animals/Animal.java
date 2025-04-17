package classes.entities.animals;

import classes.entities.Direction;
import classes.landforms.Lake;
import classes.landforms.plants.Plant;
import classes.terrains.River;
import classes.terrains.Terrain;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;

import java.util.*;

//public abstract class Animal<T extends Pane> extends Pane {

public abstract class Animal extends Pane {

    private int price;
    protected double x;
    protected double y;
    protected double speed;


    protected double restingTimePassed = 0.0;

    //ANIMAL MOVEMENT
    protected AnimalState state;
    private AnimalState previousState;
    protected Terrain target;
    protected Terrain start;
    ArrayList<Terrain> path = new ArrayList<>();
    int pathIndex = 0;

    //stats
    private int age;
    protected double thirst = 100.0;
    protected double hunger = 100.0;
    //

    //Images of the Animal, ui
    private Image spriteSheet;
    private Image walkDownImages[] = new Image[3];
    private Image walkLeftImages[] = new Image[3];
    private Image walkRightImages[] = new Image[3];
    private Image walkUpImages[] = new Image[3];
    int imageDelay = 0;
    int currentImage = 0;
    private ImageView imageView;
    private int frameWidth;
    private int frameHeight;
    protected Direction currentDirection = Direction.RIGHT;

    //State images
    //private ImageView stateIconView;
    //private static final Image dropletImage = new Image(Animal.class.getResource("/images/droplet.png").toExternalForm()); // Put in resources



    public Animal(double x, double y, int frameWidth, int frameHeight, String imgUrl, double speed, int price)  {
        this.frameWidth = frameWidth;
        this.frameHeight = frameHeight;
        this.speed = speed;
        this.price = price;

        //Animation pictures
        this.spriteSheet = new Image(getClass().getResource(imgUrl).toExternalForm());
        loadStaticDirectionImages();

        //UI
        imageView = new ImageView(walkRightImages[0]);
        imageView.setFitWidth(frameWidth * 0.6);
        imageView.setFitHeight(frameHeight * 0.6);
        getChildren().add(imageView);

        //mouse click will be the middle
        //x and y is feet
        setLayoutX(x - (frameWidth * 0.6 / 2.0));
        setLayoutY(y - (frameHeight * 0.6 / 2.0));
        this.x = x;
        this.y = y + (frameHeight * 0.6 / 2.0);

        //state
        this.state = AnimalState.IDLE;

        //state images
        /*
        stateIconView = new ImageView(dropletImage);
        stateIconView.setFitWidth(48); // Adjust size as needed
        stateIconView.setFitHeight(48);
        stateIconView.setVisible(false); // Hide by default
        stateIconView.setLayoutX(imageView.getLayoutX() + imageView.getFitWidth() / 2 - stateIconView.getFitWidth() / 2);
        stateIconView.setLayoutY(imageView.getLayoutY() - 15);
        getChildren().add(stateIconView);
        */


    }

    private void loadStaticDirectionImages() {
        for (int i = 0; i < 3; i++) {
            walkDownImages[i] = new WritableImage(spriteSheet.getPixelReader(), i * frameWidth, 0 * frameHeight, frameWidth, frameHeight);
        }
        for (int i = 0; i < 3; i++) {
            walkLeftImages[i] = new WritableImage(spriteSheet.getPixelReader(), i * frameWidth, 1 * frameHeight, frameWidth, frameHeight);
        }
        for (int i = 0; i < 3; i++) {
            walkRightImages[i] = new WritableImage(spriteSheet.getPixelReader(), i * frameWidth, 2 * frameHeight, frameWidth, frameHeight);
        }
        for (int i = 0; i < 3; i++) {
            walkUpImages[i] = new WritableImage(spriteSheet.getPixelReader(), i * frameWidth, 3 * frameHeight, frameWidth, frameHeight);
        }
    }


    /*
    public void moveTowardsTarget(boolean choose_x) {
        if (paused) {
            return;
        }

        double dx = targetX - x;
        double dy = targetY - y;

        if (Math.abs(dx) < 5 && Math.abs(dy) < 5) {
            resting = true;
            return;
        }

        if (choose_x) {
            if ((Math.abs(dx) > 1)) {
                if (dx > 0) {
                    move(Direction.RIGHT, speed, 0);
                } else {
                    move(Direction.LEFT, -speed, 0);
                }
            }
            else if (Math.abs(dy) > 1) {
                if (dy > 0) {
                    move(Direction.DOWN, 0, speed);
                } else {
                    move(Direction.UP, 0, -speed);
                }
            }
        } else {
            if (Math.abs(dy) > 1) {
                if (dy > 0) {
                    move(Direction.DOWN, 0, speed);
                } else {
                    move(Direction.UP, 0, -speed);
                }
            }
            else if ((Math.abs(dx) > 1)) {
                if (dx > 0) {
                    move(Direction.RIGHT, speed, 0);
                } else {
                    move(Direction.LEFT, -speed, 0);
                }
            }
        }

    }*/
    //eredeti moveTowards



    //===== ANIMAL MOVEMENT & ACTIVITIES =====
    //moving
    public void moveTowardsTarget() {
        // If we've reached the end of the path
        if (pathIndex >= path.size()) {
            state = determineStateFromTarget(target);
            return;
        }

        Terrain nextTile = path.get(pathIndex);
        double targetX = nextTile.getLayoutX() + nextTile.getSize() / 2.0;
        double targetY = nextTile.getLayoutY() + nextTile.getSize() / 2.0;

        double dx = targetX - this.x;
        double dy = targetY - this.y;

        // If we are close enough to this tile, move to next
        if (Math.abs(dx) < 1 && Math.abs(dy) < 1) {
            pathIndex++;
            return;
        }

        // Normalize direction for consistent speed
        double dist = Math.hypot(dx, dy);
        double stepX = (dx / dist) * speed;
        double stepY = (dy / dist) * speed;

        // Determine direction for animation
        Direction dir;
        if (Math.abs(dx) > Math.abs(dy)) {
            dir = (dx > 0) ? Direction.RIGHT : Direction.LEFT;
        } else {
            dir = (dy > 0) ? Direction.DOWN : Direction.UP;
        }

        move(dir, stepX, stepY);

    }
    public void move(Direction dir, double dx, double dy) {
        this.currentDirection = dir;
        this.x += dx; this.y += dy;


        //the UI element itself
        setLayoutX(getLayoutX() + dx);
        setLayoutY(getLayoutY() + dy);


        switch (currentDirection) {
            case UP -> {
                imageDelay++;
                if (imageDelay >= 10) {
                    currentImage = (currentImage + 1) % walkUpImages.length;
                    imageDelay = 0;
                }
                imageView.setImage(walkUpImages[currentImage]);
            }
            case DOWN -> {
                imageDelay++;
                if (imageDelay >= 10) {
                    currentImage = (currentImage + 1) % walkDownImages.length;
                    imageDelay = 0;
                }
                imageView.setImage(walkDownImages[currentImage]);
            }
            case RIGHT -> {
                imageDelay++;
                if (imageDelay >= 10) {
                    currentImage = (currentImage + 1) % walkRightImages.length;
                    imageDelay = 0;
                }
                imageView.setImage(walkRightImages[currentImage]);
            }
            case LEFT -> {
                // cycle through left frames
                imageDelay++;
                if (imageDelay >= 10) {
                    currentImage = (currentImage + 1) % walkLeftImages.length;
                    imageDelay = 0;
                }
                imageView.setImage(walkLeftImages[currentImage]);
            }
        }
    }

    //not moving states
    public void rest() {
        restingTimePassed += 0.05;
        if (restingTimePassed >= 15) {
            restingTimePassed = 0.0;
            state = AnimalState.IDLE;
        }

    }
    public void eat(){
        this.changeHunger(0.5); //+10 / mp
        if (hunger > 99.0) {
            state = AnimalState.IDLE;
        }
    }

    public void drink(){
        this.changeThirst(0.5); //+10 / mp
        //stateIconView.setVisible(true);
        if (thirst > 99.0) {
            state = AnimalState.IDLE;
            //stateIconView.setVisible(false);
        }
    }
    public void changeThirst(double val){
        this.thirst += val;
    }
    public void changeHunger(double val){
        this.hunger += val;
    }

    //setting up for dijkstra,
    public void preparePath(Terrain[][] map, ArrayList<Terrain> desiredTerrains) {
        Terrain startingTerrain = pickStartingTerrain(map);
        this.start = startingTerrain;

        Terrain targetTerrain = desiredTerrains.get(new Random().nextInt(desiredTerrains.size()));
        System.out.println("Preparing path to " + targetTerrain.getClass().getSimpleName() +
                " | Available targets: " + desiredTerrains.size());
        this.target = targetTerrain;
    }
    public Terrain pickStartingTerrain(Terrain[][] map){
        Terrain closest = null;
        double minDistance = Double.MAX_VALUE;
        int TILE_SIZE = 30;


        for (int col = 0; col < map.length; col++) {
            for (int row = 0; row < map[0].length; row++) {
                Terrain tile = map[col][row];

                double centerX = tile.getLayoutX() + TILE_SIZE / 2.0;
                double centerY = tile.getLayoutY() + TILE_SIZE / 2.0;

                // Compare distances
                double distance = Math.hypot(this.x - centerX, this.y - centerY);
                if (distance < minDistance) {
                    minDistance = distance;
                    closest = tile;
                }
            }
        }

        return closest;
    }
    public void setPath(ArrayList<Terrain> path){
        this.path = path;
        this.pathIndex = 0;
    }

    //managing states
    public void transitionTo(AnimalState newState) {
        if (newState == AnimalState.PAUSED) {
            previousState = state;
        }
        this.state = newState;
    }
    public void resume() {
        if (state == AnimalState.PAUSED && previousState != null) {
            transitionTo(previousState);
        }
    }
    private AnimalState determineStateFromTarget(Terrain target) {
        if(target instanceof River){
            return AnimalState.DRINKING;
        }
        else if (target.hasLandform()) {
            if (target.getLandform() instanceof Lake) return AnimalState.DRINKING;
            if (target.getLandform() instanceof Plant) return AnimalState.EATING;
        }
        return AnimalState.RESTING;
    }
    //=====================================




//rest and pickNewTarget csak növényekre
    /*public void rest(ArrayList<Plant> plants) {
        restingTimePassed += 0.05; // updateAnimalPositions() is 50ms

        if (restingTimePassed >= restDuration) {
            resting = false;
            restingTimePassed = 0.0;
            pickNewTarget(plants);
        }
    }

    public void pickNewTarget(ArrayList<Plant> plants) {
        Random random = new Random();
        Plant randomPlant = plants.get(random.nextInt(plants.size()));

        this.targetX = randomPlant.getX() + (double )(randomPlant.getTileSize() / 2);  //* randomPlant.getTileSize(); //* randomPlant.getWidthInTiles(); //randomPlant.getLayoutX();
        this.targetY = randomPlant.getY() + (double )(randomPlant.getTileSize() / 2);  //* randomPlant.getTileSize(); //* randomPlant.getHeightInTiles(); //randomPlant.getLayoutY();
       // System.out.println("target picked: " + targetX +  " : " +targetY);

    }*/

    /*
    public abstract void rest(ArrayList<T> panes);*/
    /*
    public abstract void pickNewTarget(ArrayList<T> panes);
    */
    public void sellAnimal(){
        System.out.println(this.getClass() + " sold");
        //TODO sell animal
    }


    //Getters, Setters
    public double getSpeed(){
        return this.speed;
    }
    public int getPrice() { return this.price; }
    public int getFrameWidth(){
        return this.frameWidth;
    }
    public int getFrameHeight(){
        return this.frameHeight;
    }
    public double getX(){
        return this.x;
    }
    public double getY(){
        return this.y;
    }
    public ImageView getImageView(){
        return this.imageView;
    }
    public AnimalState getState(){
        return this.state;
    }
    public Terrain getStart(){
        return start;
    }
    public Terrain getTarget(){
        return target;
    }
    public ArrayList<Terrain> getPath(){
        return this.path;
    }
    public double getDepth(){
        return this.y + (frameHeight * 0.6 / 2.0);
    }
    public double getThirst(){
        return thirst;
    }
    public double getHunger(){
        return hunger;
    }
}