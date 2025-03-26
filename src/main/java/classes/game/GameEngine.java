package classes.game;

import classes.Difficulty;
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
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

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
    private int touristCount = 0;
    private int jeepCount = 0;
    private int ticketPrice = 0;




    public void gameLoop() {

        Timeline timeline = new Timeline(
            new KeyFrame(Duration.millis(50), e -> {
                // 1️⃣ Move animals
                updateAnimalPositions();
                updateHumanPositions();
                sortDynamicLayer();

                // 2️⃣ Update herds
                //updateHerds();

                // 3️⃣ Possibly spawn tourists/poachers
                //maybeSpawnTourist();
                //maybeSpawnPoacher();

                // 4️⃣ Update money, time, conditions
                //updateGameConditions();

                // 5️⃣ Check win/lose conditions
                if (gameOver()) {
                    //handleGameOver();
                } else if (gameWon()) {
                    //handleGameWin();
                }

                // UI sync, Display things
                spentTime += 0.05;
                gameController.updateDisplay(
                        spentTime, carnivores.size(), herbivores.size(), jeepCount, touristCount, ticketPrice
                );

            })
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }


    private void sortDynamicLayer() {
        Pane dynamicLayer = gameBoard.getDynamicLayer();

        List<Node> sortedNodes = new ArrayList<>(dynamicLayer.getChildren());
        //without reversed min -> max, with reversed max -> min
        sortedNodes.sort(Comparator.comparingDouble(this::extractDepthY));

        /*
        System.out.println();
        for (Node node : sortedNodes) {
            double depth = extractDepthY(node);
            System.out.println(node.getClass().getSimpleName() + " @ depthY: " + depth);
        }*/


        Platform.runLater(() -> {
            dynamicLayer.getChildren().setAll(sortedNodes);
        });
    }
    private double extractDepthY(Node node) {
        if (node instanceof Animal animal) {
            return animal.getY();
        } else if(node instanceof Landform landform)
            return landform.getDepth();
        else
            return 1.0;

    }
    public void buyAnimal(Animal animal){
        if(animal instanceof Herbivore)
            this.herbivores.add((Herbivore) animal);
        else
            this.carnivores.add((Carnivore) animal);
    }
    private void updateAnimalPositions() {
        for (Herbivore herbivore : herbivores) {
            if(!herbivore.getResting())
                herbivore.moveTowardsTarget();
            else
                herbivore.rest(1920, 930);
        }

        for (Carnivore carnivore : carnivores) {
            if(!carnivore.getResting())
                carnivore.moveTowardsTarget();
            else
                carnivore.rest(1920, 930);
        }
    }

    public void buyRanger(Ranger ranger){
        this.rangers.add(ranger);
    }
    private void updateHumanPositions() {
        for (Ranger ranger : rangers) {
            //TODO if for when not following target
            ranger.moveTowardsTarget();

        }

        for (Poacher poacher : poachers) {
            //TODO if for when not following target
            poacher.moveTowardsTarget();
        }
    }




    public int money;

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
    private ArrayList<Road> roads;




    public GameEngine(GameController gameController, Difficulty difficulty, GameBoard gameBoard) {
        this.gameController = gameController;
        this.difficulty = difficulty;
        this.gameBoard = gameBoard;


        money = 0;
        ticketPrice = 0;
        spentTime = 0;
        winningDays = 0;
        animalCount = 0;
        touristCount = 0;
        jeepCount = 0;
        carnivores = new ArrayList<Carnivore>();
        herbivores = new ArrayList<Herbivore>();
        herds = new ArrayList<Herd>();
        rangers = new ArrayList<Ranger>();
        poachers = new ArrayList<Poacher>();
        herds = new ArrayList<Herd>();
        jeeps = new ArrayList<Jeep>();
        roads = new ArrayList<Road>();

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

    public boolean gameOver() {
        return false;
    }

    public boolean gameWon() {
        return false;
    }

    public void startsJeep() {

    }



    public void pays(Ranger ranger) {
        //rangers.indexOf(ranger).paid = true;
        money = money - 0;
        //actual érték needed

    }

    /*
    public void sells(Animal animal) {
        money = money + 0;
        //actual érték needed

        if (animal instanceof Herbivore) {
            herbivores.remove(animal);
        } else {
            carnivores.remove(animal);
        }
    }*/


}
