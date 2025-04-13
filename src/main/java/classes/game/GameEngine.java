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
    private int money = 5000;
    private final Random rand = new Random();



    public GameEngine(GameController gameController, Difficulty difficulty, Pane terrainLayer, Pane uiLayer) {
        this.gameController = gameController;
        this.difficulty = difficulty;

        this.gameBoard = new GameBoard(terrainLayer, uiLayer);
        gameBoard.setupGroundBoard();
        gameBoard.generatePlants(rand.nextInt(10) + 10);


        money = 5000;
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
        //System.out.println(difficulty);

        savePlants();

        Timeline timeline = new Timeline(
            new KeyFrame(Duration.millis(50), e -> {

                updateAnimalStates();
                updateHumanPositions();
                updateJeepPositions();
                sortUiLayer();


                maybeSpawnTourist();

                // Check win/lose conditions
                if (gameOver()) {
                    //handleGameOver();
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

    //===SPAWNING TOURISTS
    private void maybeSpawnTourist() {
        double chancePerSecond = 0.05 * (herbivores.size() + (carnivores.size()) * 2);
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
    //==========

    //===VISUALS
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
    //==========


    private void updateAnimalStates() {
        for (Herbivore herbivore : herbivores) {
            herbivore.changeThirst(-0.01);
            herbivore.changeHunger(-0.03);

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
    }
    private void updateHumanPositions() {


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
        for (Tourist tourist : tourists){
            tourist.changeVisitDuration(0.1);

            switch (tourist.getState()){
                case MOVING, EXITING -> tourist.moveTowardsTarget();
                case RESTING -> tourist.rest();
                case LEFT -> {
                    gameController.removeTourist(tourist);
                    tourists.remove(tourist);
                }
                case IDLE -> {
                    tourist.pickNewTarget();
                }
            }
        }
    }



    //RANGER
    public void buyRanger(Ranger ranger){
        this.rangers.add(ranger);
    }



    //JEEP
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
    }


    //public void sellAnimal(Animal<? extends Pane> animal) {
    public void sellAnimal(Animal animal) {
        money += animal.getPrice();

        if (animal instanceof Herbivore) {
            herbivores.remove(animal);
        } else {
            carnivores.remove(animal);
        }

        Pane uiLayer = gameBoard.getUiLayer();
        uiLayer.getChildren().remove(animal);

        System.out.println("Eladtál egy állatot " + animal.getPrice() + " pénzért.");
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
        return false;
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

    public void pays(Ranger ranger) {
        //rangers.indexOf(ranger).paid = true;
        money = money - 100;

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
