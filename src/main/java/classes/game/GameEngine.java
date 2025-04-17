package classes.game;

import classes.Difficulty;
import classes.entities.additions.InfoWindowAnimal;
import classes.entities.additions.InfoWindowRanger;
import classes.entities.human.*;
import classes.entities.animals.*;
import classes.Jeep;
import classes.Speed;
import classes.landforms.*;
import classes.controllers.GameController;
import classes.landforms.plants.Plant;
import classes.terrains.Terrain;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import static classes.Difficulty.EASY;
import static classes.Difficulty.MEDIUM;
import static classes.entities.animals.AnimalState.*;

/* todo
    - difficulty: konstruktorban kell megkapja, valahogy majd a difficultySCrrenből jön át
    - waterSources: szintén kap konstruktorból is de lehelyezéskor is idekerülnel
        - lehet minden x,y-t tárolni ahol van víz, lehet tile alapon, vagy akár egész nagy méretekben is
    - paths(?): kiszámolni és valamiben eltárolni a lehetséges jeep útvonalakat
notes:
    - winningDays: lehet daysTillWIn és akkor majd mindig ki kell vonogatni aztán annyi
    - spentTIme: órában right?
    - conditions: [0]:touristCount, [1]:money, [2]:HerbiCount, [3]:CarniCount
 */

public class GameEngine {
    private GameController gameController;
    private GameBoard gameBoard;

    private double spentTime;
    protected ArrayList<Carnivore> carnivores;
    protected ArrayList<Herbivore> herbivores;
    protected ArrayList<Tourist> tourists;
    private int jeepCount ;
    private int ticketPrice;
    private int money = 100000;
    private final Random rand = new Random();

    private boolean canCheckForLose = false;



    public GameEngine(GameController gameController, Difficulty difficulty, Pane terrainLayer, Pane uiLayer) {
        this.gameController = gameController;
        this.difficulty = difficulty;

        this.gameBoard = new GameBoard(terrainLayer, uiLayer);
        gameBoard.setupGroundBoard();
        gameBoard.generatePlants(rand.nextInt(10) + 10);


        carnivores = new ArrayList<Carnivore>();
        herbivores = new ArrayList<Herbivore>();
        tourists = new ArrayList<Tourist>();
        herds = new ArrayList<Herd>();
        rangers = new ArrayList<Ranger>();
        poachers = new ArrayList<Poacher>();
        herds = new ArrayList<Herd>();
        jeeps = new ArrayList<Jeep>();
        roads = new ArrayList<Road>();
        plants = new ArrayList<Plant>();

        choose_x = Math.random() < 0.5;
        frameCounter = 0;

        //todo: actual értékek
        entrance = new Pair<>(0, 0);
        exit = new Pair<>(0, 0);
        conditions = new ArrayList<Integer>();
        if (difficulty == EASY) {
            conditions.add(0);
            conditions.add(0);
            conditions.add(0);
            conditions.add(0);
        } else if (difficulty == MEDIUM) {
            conditions.add(0);
            conditions.add(0);
            conditions.add(0);
            conditions.add(0);
        } else {
            conditions.add(0);
            conditions.add(0);
            conditions.add(0);
            conditions.add(0);
        }
    }

    public void gameLoop() {
        savePlants();

        Timeline timeline = new Timeline(
            new KeyFrame(Duration.millis(50), e -> {

                updateAnimalStates();
                updateHumanStates();
                updateJeepPositions();
                sortUiLayer();


                maybeSpawnTourist();

                // Check win/lose conditions
                if (gameOver()) {
                    gameController.openLosePane();
                } else if (gameWon()) {
                    //handleGameWin();
                }

                // UI sync, Display things
                spentTime += 0.05;
                gameController.updateDisplay(spentTime, carnivores.size(), herbivores.size(), jeepCount, tourists.size(), ticketPrice, money);

            })
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    // ==== SPAWNING TOURISTS
    private void maybeSpawnTourist() {
        double chancePerSecond = 0.01 * (herbivores.size() + (carnivores.size()) * 2);
        double spawnChancePerTick = chancePerSecond / 20.0;

        //cap it, max is 1 visitor every 2 secs but then again its random so
        spawnChancePerTick = Math.min(spawnChancePerTick, 0.025);

        if (rand.nextDouble() < spawnChancePerTick) {
            Platform.runLater(() -> {
                Tourist tourist = gameController.spawnTourist();
                tourists.add(tourist);
            });
        }

    }
    // =====

    // ==== VISUALS
    private void sortUiLayer() {
        Pane uiLayer = gameBoard.getUiLayer();

        List<Node> sortedNodes = new ArrayList<>(uiLayer.getChildren());
        //without reversed min -> max, with reversed max -> min
        sortedNodes.sort(Comparator.comparingDouble(this::extractDepthY));


        Platform.runLater(() -> {
            uiLayer.getChildren().setAll(sortedNodes);
        });
    }
    private double extractDepthY(Node node) {
        if (node instanceof Animal animal)
            return animal.getY();
        else if(node instanceof Human human)
                return human.getY();
        else if(node instanceof Landform landform)
            return landform.getDepth();
        else if (node instanceof InfoWindowAnimal || node instanceof InfoWindowRanger)
            return Double.MAX_VALUE;
        else if (node instanceof Jeep jeep)
            return jeep.getDepth();
        else
            return 0;
    }
    // =====


    // ==== UPDATE STATES
    private void updateAnimalStates() {
        for (Herbivore herbivore : herbivores) {
            herbivore.changeThirst(-0.3);
            herbivore.changeHunger(-0.5);

            switch(herbivore.getState()){
                case MOVING -> herbivore.moveTowardsTarget();
                case RESTING -> herbivore.rest();
                case EATING -> herbivore.eat();
                case DRINKING -> herbivore.drink();
                case PAUSED -> {}
                case IDLE -> {
                    if (herbivore.getThirst() < 25.0) {
                        herbivore.preparePath(gameBoard.getTerrainGrid(), gameBoard.getLakeTerrains());
                    } else if (herbivore.getHunger() < 25.0) {
                        herbivore.preparePath(gameBoard.getTerrainGrid(), gameBoard.getPlantTerrains());
                    } else {
                        herbivore.preparePath(gameBoard.getTerrainGrid(), gameBoard.getGroundTerrains());
                    }
                    ArrayList<Terrain> path = gameBoard.findPathDijkstra(herbivore.getStart(), herbivore.getTarget());
                    herbivore.setPath(path);
                    herbivore.transitionTo(MOVING);

                    System.out.println(herbivore.getPath());
                }
            }
        }

        for (Carnivore carnivore : carnivores){
            carnivore.changeThirst(-0.01);
            carnivore.changeHunger(-0.02);
            System.out.println(carnivore.getHunger());

            switch (carnivore.getState()){
                case MOVING -> carnivore.moveTowardsTarget();
                case RESTING -> carnivore.rest();
                case EATING -> {
                    carnivore.changeHunger(0.5);
                    if (carnivore.getHunger() > 99.0) {
                        carnivore.transitionTo(RESTING);
                        gameController.removeHerbivore(carnivore.getPrey());
                        herbivores.remove(carnivore.getPrey());
                    }
                }
                case DRINKING -> carnivore.drink();
                case PAUSED -> {}
                case HUNTING -> {
                    carnivore.huntTarget();
                }
                case IDLE -> {
                    ArrayList<Terrain> path;
                    if (carnivore.getThirst() < 25.0) {
                        carnivore.preparePath(gameBoard.getTerrainGrid(), gameBoard.getLakeTerrains());

                        path = gameBoard.findPathDijkstra(carnivore.getStart(), carnivore.getTarget());
                        carnivore.setPath(path);
                        carnivore.transitionTo(MOVING);
                        System.out.println(carnivore.getPath());
                    } else if (carnivore.getHunger() < 25.0) {
                        carnivore.choosePrey(herbivores);
                        carnivore.transitionTo(HUNTING);
                    } else {
                        carnivore.preparePath(gameBoard.getTerrainGrid(), gameBoard.getGroundTerrains());

                        path = gameBoard.findPathDijkstra(carnivore.getStart(), carnivore.getTarget());
                        carnivore.setPath(path);
                        carnivore.transitionTo(MOVING);
                        System.out.println(carnivore.getPath());
                    }
                }
            }
        }
    }
    private void updateHumanStates() {

        for (Ranger ranger : rangers) {
            switch (ranger.getState()){
                case MOVING -> ranger.moveTowardsTarget();
                case RESTING -> ranger.rest();
                case PAUSED -> {}
                case IDLE -> {
                    ranger.pickNewTarget();
                    ranger.transitionTo(HumanState.MOVING);
                }
            }

            if (ranger.isDueForPayment(spentTime)) {
                payRanger(ranger);
            }

        }
        for (Poacher poacher : poachers) {
            switch (poacher.getState()){
                case MOVING -> poacher.moveTowardsTarget();
                case RESTING -> poacher.rest();
                case PAUSED -> {}
                case IDLE -> {
                    poacher.pickNewTarget();
                    poacher.transitionTo(HumanState.MOVING);
                }
            }
        }

        List<Tourist> toRemove = new ArrayList<>();
        for (Tourist tourist : tourists){
            tourist.changeVisitDuration(2);

            switch (tourist.getState()){
                case MOVING, EXITING -> tourist.moveTowardsTarget();
                case RESTING -> tourist.rest();
                case LEFT -> {
                    toRemove.add(tourist);

                }
                case IDLE -> {
                    tourist.pickNewTarget();
                }
            }
        }
        for (Tourist t : toRemove) {
            gameController.removeTourist(t);
            tourists.remove(t);
        }

    }
    // =====


    // ==== RANGERS
    public void buyRanger(Ranger ranger){
        ranger.setLastPaidHour(spentTime);
        this.rangers.add(ranger);
        payRanger(ranger);
    }

    public void payRanger(Ranger ranger) {
        money -= 5000;
        ranger.setLastPaidHour(spentTime);
    }
    // =====


    // ==== JEEPS
    public void buyJeep() {
/*        if (touristCount >= 4) {
            touristCount -= 4;
            startJeep();
        } else {
            System.out.println("Not enough tourists");
        }*/
        startJeep();
    }
    public void startJeep() {
        jeepCount++;
        Jeep jeep = new Jeep(150, 0);
        // jeep = new Jeep(1770, 900);
        jeeps.add(jeep);
        gameBoard.getUiLayer().getChildren().add(jeep);
        gameController.updateDisplay(spentTime, carnivores.size(), herbivores.size(), jeepCount, tourists.size(), ticketPrice, money);
    }
    public void updateJeepPositions() {
        for (Jeep jeep : jeeps) {
            jeep.autoMove(roads);
        }
    }
    // =====


    public void buyPlant(Landform placesPlant) {
        plants.add((Plant) placesPlant); //konverzió elég funky
        //System.out.println("plant added to list");
    }

    //public void buyAnimal(Animal<? extends Pane> animal) {
    public void buyAnimal(Animal animal) {
        if (animal instanceof Herbivore herbivore) {
            this.herbivores.add(herbivore);
        } else if (animal instanceof Carnivore carnivore) {
            this.carnivores.add(carnivore);
        }
        canCheckForLose = true;
    }


    //public void sellAnimal(Animal<? extends Pane> animal) {
    public void sellAnimal(Animal animal) {
        money += animal.getPrice();

        if (animal instanceof Herbivore) {
            herbivores.remove(animal);
        } else {
            carnivores.remove(animal);
        }

        System.out.println("You sold an animal for " + animal.getPrice() + "$");
    }


    public int winningDays;
    public Difficulty difficulty;
    public int animalCount;

    public Pair<Integer, Integer> entrance;
    public Pair<Integer, Integer> exit;
    public ArrayList<Plant> plants;
    //todo: nagyon random a típus
    public ArrayList<Pair<Integer, Integer>> waterSources;

    private Speed speed;
    private ArrayList<Integer> conditions;

    private ArrayList<Herd> herds;
    private ArrayList<Poacher> poachers;
    private ArrayList<Ranger> rangers;
    private ArrayList<Jeep> jeeps;
    public ArrayList<Road> roads;

    private boolean choose_x;
    private int frameCounter;


    public boolean gameOver() {
        return (canCheckForLose && (herbivores.isEmpty() && carnivores.isEmpty())) || money <= 0;
    }

    public boolean gameWon() {
        return false;
    }

    public GameBoard getGameBoard(){
        return this.gameBoard;
    }

    public void addTourist(Tourist t){
        tourists.add(t);
    }

    public void savePlants() {
        for (Node node : gameBoard.getUiLayer().getChildren()) {
            if (node instanceof Plant) {
                plants.add((Plant) node);
            }
        }
    }

    public boolean haveEnoughMoneyForAnimal(Animal animalInstance) {
        return true;
    }
}
