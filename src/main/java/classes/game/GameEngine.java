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
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
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
    protected ArrayList<Tourist> tourists = new ArrayList<>();
    private int touristCount;
    private int jeepCount ;
    private int ticketPrice;
    private int money = 5000;
    private final Random rand = new Random();


    public GameEngine(GameController gameController, Difficulty difficulty, Pane terrainLayer, Pane uiLayer, Pane ghostLayer) {
        this.gameController = gameController;
        this.difficulty = difficulty;

        this.gameBoard = new GameBoard(terrainLayer, uiLayer, ghostLayer);
        gameBoard.setupGroundBoard();
        gameBoard.generatePlants(rand.nextInt(10) + 10);


        money = 5000;
        carnivores = new ArrayList<Carnivore>();
        herbivores = new ArrayList<Herbivore>();
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
        System.out.println(difficulty);

        savePlants();

        Timeline timeline = new Timeline(
            new KeyFrame(Duration.millis(50), e -> {
                // Move animals
                frameCounter++;
                if (frameCounter % 100 == 0) {
                    frameCounter = 0;
                    choose_x = Math.random() < 0.5;
                    //System.out.println("choose_x changed");
                    //System.out.println(choose_x);
                }
                updateAnimalPositions(choose_x);
                updateHumanPositions();
                updateJeepPositions();
                sortUiLayer();

                // Update herds
                //updateHerds();

                // Possibly spawn tourists/poachers
                //maybeSpawnTourist();

                // Check win/lose conditions
                if (gameOver()) {
                    //handleGameOver();
                } else if (gameWon()) {
                    //handleGameWin();
                }

                // UI sync, Display things
                spentTime += 0.05;
                gameController.updateDisplay(spentTime, carnivores.size(), herbivores.size(), jeepCount, touristCount, ticketPrice, money);

            })
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void maybeSpawnTourist() {
        double spawnChancePerTick = 0.1;
        if (Math.random() < spawnChancePerTick){
            touristCount++;
            gameController.spawnTourist();
            //tourists.add(tourist);
        }

    }

    public void setTourist(Tourist tourist){
        this.tourists.add(tourist);
    }


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
        else
            return 0;
    }

    private void updateAnimalPositions(boolean choose_x) {
        for (Herbivore herbivore : herbivores) {
            if(!herbivore.getResting())
                herbivore.moveTowardsTarget(choose_x);
            else
                //herbivore.rest(1920, 930);
                herbivore.rest(plants);
        }
        for (Carnivore carnivore : carnivores) {
            if(!carnivore.getResting() && !herbivores.isEmpty()) {
                //System.out.println("sent animal coz notEmpty");
                carnivore.updateTarget();
                carnivore.moveTowardsTarget(choose_x);
            } else {
                //carnivore.rest(1920, 930);
                carnivore.rest(herbivores);
                //System.out.println("called rest on empty list");
            }

        }
    }

    //RANGER
    public void buyRanger(Ranger ranger){
        this.rangers.add(ranger);
    }

    private void updateHumanPositions() {
        for (Ranger ranger : rangers) {
            //TODO if for when not following target
            ranger.moveTowardsTarget(1920, 930);

        }
        for (Poacher poacher : poachers) {
            //TODO if for when not following target
            poacher.moveTowardsTarget(1920, 930);
        }

        for (Tourist tourist : tourists){
            if(!tourist.getResting())
                tourist.moveTowardsTarget(0,0);
            else
                tourist.rest(0,0);
        }
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
        gameController.updateDisplay(spentTime, carnivores.size(), herbivores.size(), jeepCount, touristCount, ticketPrice, money);
    }
    public void updateJeepPositions() {
        for (Jeep jeep : jeeps) {
            jeep.autoMove(roads);
        }
    }

    public void buyRanger(Ranger ranger) {
        this.rangers.add(ranger);
    }

    //todo: jeep méretek
    public void addJeep() {
        jeepCount++;
        jeeps.add(new Jeep(100, 100, 300));
        gameController.updateDisplay(spentTime, carnivores.size(), herbivores.size(), jeepCount, touristCount, ticketPrice, money);
    }


    public void buyPlant(Landform placesPlant) {
        plants.add((Plant) placesPlant); //konverzió elég funky
        //System.out.println("plant added to list");
    }

    public void buyAnimal(Animal<? extends Pane> animal) {

        if (animal instanceof Herbivore herbivore) {
            herbivore.pickNewTarget(plants);
            this.herbivores.add(herbivore);
            //this.herbivores.add((Herbivore) animal);
        } else if (animal instanceof Carnivore carnivore) {
            if (!herbivores.isEmpty()) {
                carnivore.pickNewTarget(herbivores);
            }
            this.carnivores.add(carnivore);

        }
    }


    public void sellAnimal(Animal<? extends Pane> animal) {
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
        System.out.println("Eladtál egy állatot " + animal.getPrice() + " pénzért.");
    }

    public boolean haveEnoughMoneyForAnimal(Animal animalInstance) {
        return true;
    }
}
