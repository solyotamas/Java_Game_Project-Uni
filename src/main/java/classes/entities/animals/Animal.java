package classes.entities.animals;

import classes.entities.Direction;
import classes.landforms.Lake;
import classes.landforms.plants.Plant;
import classes.terrains.River;
import classes.terrains.Terrain;
import javafx.scene.Cursor;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;

import java.util.*;
import java.util.Random;

//public abstract class Animal<T extends Pane> extends Pane {

public abstract class Animal extends Pane {

    protected int price;
    protected double x;
    protected double y;
    protected double speed;
    protected int appetite;


    protected double restingTimePassed = 0.0;

    //ANIMAL MOVEMENT
    protected AnimalState state;
    private AnimalState previousState;
    protected Terrain target;
    protected Terrain start;
    ArrayList<Terrain> path = new ArrayList<>();
    int pathIndex = 0;

    //stats
    protected int age;
    protected int startingAge = 5;
    protected int lifeExpectancy;
    protected double bornAt;
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
    private ImageView stateIcon;
    private Image thirstImage;
    private Image hungerImage;
    private Image sleepImage;

    //herd
    protected boolean isInAHerd;
    protected Herd herd = null;

    private boolean isManuallyPaused = false;
    private boolean isBeingEaten = false;

    private boolean isStarving = false;
    private double starvingTime = 0.0;

    private int behindHerdLeader;
    private final Random rand = new Random();

    public Animal(double x, double y, int frameWidth, int frameHeight, String imgUrl, double speed, int price, int lifeExpectancy)  {
        this.frameWidth = frameWidth;
        this.frameHeight = frameHeight;
        this.speed = speed;
        this.price = price;
        this.lifeExpectancy = lifeExpectancy;
        this.setCursor(Cursor.HAND);

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
        loadStateImages();
        this.stateIcon = new ImageView();
        stateIcon.setVisible(false);
        stateIcon.setLayoutX(imageView.getLayoutX() + imageView.getFitWidth() / 2 - stateIcon.getFitWidth() / 2);
        stateIcon.setLayoutY(imageView.getLayoutY() - 5);
        getChildren().add(stateIcon);

        //herd
        this.behindHerdLeader = rand.nextInt(15) + 15;

    }

    //Image loaders
    private void loadStateImages(){
        this.thirstImage = new Image(Animal.class.getResource("/images/thirst.png").toExternalForm());
        this.hungerImage = new Image(Animal.class.getResource("/images/hunger.png").toExternalForm());
        this.sleepImage = new Image(Animal.class.getResource("/images/sleep.png").toExternalForm());
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




    //===== ANIMAL MOVEMENT & ACTIVITIES =====
    //moving
    public void moveTowardsTarget(Terrain terrain) {
        if (isStarving) {
            transitionTo(AnimalState.IDLE);
        }

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

        move(terrain, dir, stepX, stepY);

    }
    public void move(Terrain terrain, Direction dir, double dx, double dy) {
        //todo ELEGÁNSABB LENNE A DIRECTION-BŐL KISZEDNI AZ IRÁNY ÉRTÉKEIT
        this.currentDirection = dir;

        //moving slower on terrains with crossingDifficulty (river, hill)
        dx *= crossingDifficulty(terrain);
        dy *= crossingDifficulty(terrain);
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

    //in a herd movements
    public void moveTowardsLeader(Animal leader, Terrain terrain) {
        double targetX = leader.getX();
        double targetY = leader.getY();

        double dx = targetX - this.x;
        double dy = targetY - this.y;

        double distance = Math.hypot(dx, dy);
        if (distance < this.behindHerdLeader) {
            this.transitionTo(leader.getState());
            return;
        }

        double stepX = (dx / distance) * speed;
        double stepY = (dy / distance) * speed;

        Direction dir;
        if (Math.abs(dx) > Math.abs(dy)) {
            dir = dx > 0 ? Direction.RIGHT : Direction.LEFT;
        } else {
            dir = dy > 0 ? Direction.DOWN : Direction.UP;
        }

        move(terrain, dir, stepX, stepY);
    }
    public double crossingDifficulty(Terrain terrain) {
        int difficulty = terrain.getCrossingDifficulty(); // 0–3
        return 1.0 / difficulty;
    }

    //not moving states
    public void rest() {
        restingTimePassed += 0.05;

        stateIcon.setImage(sleepImage);
        stateIcon.setVisible(true);



        if (restingTimePassed >= 15) {
            restingTimePassed = 0.0;
            state = AnimalState.IDLE;

            stateIcon.setVisible(false);
        }

    }
    public void eat(){
        this.changeHunger(0.5); //+10 / mp
        stateIcon.setImage(hungerImage);
        stateIcon.setVisible(true);

        // when eating plants, they lose nutrition, if nutrition is 0 the plant disappears
        if (this.target != null && this.target.getLandform() instanceof Plant) {
            Plant plant = (Plant) this.target.getLandform();
            plant.reduceNutrition(this.appetite * 0.005);

            if (plant.isDepleted()) {
                state = AnimalState.IDLE;
                stateIcon.setVisible(false);
            }
        }

        if (hunger > 99.0) {
            state = AnimalState.IDLE;
            stateIcon.setVisible(false);
        }
    }
    public void drink(){
        this.changeThirst(0.5); //+10 / mp

        stateIcon.setImage(thirstImage);
        stateIcon.setVisible(true);

        if (thirst >= 100.0) {
            thirst = 100.0;
            state = AnimalState.IDLE;
            stateIcon.setVisible(false);
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
        this.state = newState;
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
    // =====

    // ==== AGING, PRICE HANDLING, STARVING
    public void setBornAt(double currentGameHour) {
        this.bornAt = currentGameHour;
    }
    public boolean oldEnoughToDie(double currentGameHour) {
        agingAnimal(currentGameHour);
        return this.age >= lifeExpectancy;
    }

    public void agingAnimal(double currentGameHour) {
        this.age = startingAge + (int) ((currentGameHour - bornAt) / 168.0); // 24 * 7, one year is one week in game

        double ageRatio = (double) this.age / this.lifeExpectancy;
        this.appetite = (int)(1 + ageRatio * 99);
    }

    public int getSellPrice() {
        int ageSegment = lifeExpectancy / 5;
        int base = this.price * 3 / 5;

        if (this.age <= ageSegment) {
            return base * 5 / 5;
        } else if (this.age <= 2 * ageSegment) {
            return base * 4 / 5;
        } else if (this.age <= 3 * ageSegment) {
            return base * 3 / 5;
        } else if (this.age <= 4 * ageSegment) {
            return base * 2 / 5;
        } else {
            return base / 5;
        }
    }

    public void setStarving(boolean starving) {
        this.isStarving = starving;
        if (!starving) {
            this.starvingTime = 0.0;
        }
    }
    public void incrementStarvingTime(double amount) {
        if (isStarving) {
            this.starvingTime += amount;
        }
    }
    public boolean diedOfStarvation() {
        return starvingTime > 24.0; // one day
    }
    // =====

    //Getters, Setters
    public int getStartingAge() {
        return startingAge;
    }
    public int getPrice() {
        return this.price;
    }
    public int getAppetite() {
        return this.appetite;
    }
    public double getSpeed(){
        return this.speed;
    }
    public int getAge(){
        return (int) this.age;
    }
    public int getLifeExpectancy(){
        return this.lifeExpectancy;
    }
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
    public void setStateIcon(Image stateIcon){
        this.stateIcon.setImage(stateIcon);
    }
    public void setStateIconVisibility(boolean val){
        this.stateIcon.setVisible(val);
    }
    public void setThirst(double thirst){
        this.thirst = thirst;
    }
    public void setHunger(double hunger){
        this.hunger = hunger;
    }
    public void setIsInAHerd(boolean val){
        this.isInAHerd = val;
    }
    public boolean getIsInAHerd(){
        return isInAHerd;
    }

    public double getBornAt() {
        return bornAt;
    }

    public Herd getHerd() {
        return this.herd;
    }
    public void setHerd(Herd herd){
        this.herd = herd;
    }

    public void pauseManually() {
        isManuallyPaused = true;
    }

    public void resumeManually() {
        isManuallyPaused = false;
    }

    public boolean isManuallyPaused() {
        return isManuallyPaused;
    }
    public void setBeingEaten(boolean val) {
        this.isBeingEaten = val;
    }

    public boolean isBeingEaten() {
        return isBeingEaten;
    }
    public void setRestingTimePassed(double val){
        this.restingTimePassed = val;
    }

    public Direction getCurrentDirection() { return currentDirection; }
}