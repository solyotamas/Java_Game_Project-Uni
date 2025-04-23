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
import javafx.scene.image.Image;
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
    private int jeepCount ;
    private int ticketPrice;
    private int money = 5000;
    private final Random rand = new Random();

    //Lists
    protected ArrayList<Carnivore> carnivores;
    protected ArrayList<Herbivore> herbivores;

    protected ArrayList<Tourist> tourists;
    private ArrayList<Herd> carnivoreherds;
    private ArrayList<Herd> herbivoreherds;

    private ArrayList<Poacher> poachers;
    private ArrayList<Ranger> rangers;
    private ArrayList<Jeep> jeeps;
    public ArrayList<Road> roads;


    //need
    private final Image hungerForCarnivore = new Image(GameEngine.class.getResource("/images/hunger.png").toExternalForm());

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

        carnivoreherds = new ArrayList<>();
        herbivoreherds = new ArrayList<>();

        rangers = new ArrayList<Ranger>();
        poachers = new ArrayList<Poacher>();
        jeeps = new ArrayList<Jeep>();
        roads = new ArrayList<Road>();
        plants = new ArrayList<Plant>();


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
                //== ANIMALS
                updateAnimalStates();

                //Herds
                formNewHerds();
                checkToJoinHerds();
                cleanupSmallHerds(carnivoreherds);
                updateHerdStates();

                //== HUMANS
                updateHumanStates();

                //== JEEP
                updateJeepPositions();

                //== VISUALS
                sortUiLayer();

                //LOG
                //logWhatever();

                //== TOURISTS
                maybeSpawnTourist();

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
    //==========

    public void logWhatever(){
        System.out.println(herbivoreherds.size());
    }

    //===HERDS
    private void formNewHerds(){
        for (Herbivore h1 : herbivores) {
            if (h1.getIsInAHerd()) continue;
            if (h1.getState() != AnimalState.MOVING) continue;


            for (Herbivore h2 : herbivores) {
                if (h1 == h2 || h2.getIsInAHerd()) continue;
                if (!h1.getClass().equals(h2.getClass())) continue;
                if (h2.getState() != AnimalState.MOVING) continue;

                double dx = h1.getX() - h2.getX();
                double dy = (h1.getY() + h1.getImageView().getFitHeight() / 2.) -
                        (h2.getY() + h2.getImageView().getFitHeight() / 2.);

                if (Math.hypot(dx, dy) <= 150) {
                    Herd herd = new Herd(new ArrayList<>(List.of(h1, h2)));
                    herbivoreherds.add(herd);
                    break;
                }
            }
        }

        for (Carnivore c1 : carnivores) {
            if (c1.getIsInAHerd()) continue;
            if (c1.getState() != AnimalState.MOVING) continue;


            for (Carnivore c2 : carnivores) {
                if (c1 == c2 || c2.getIsInAHerd()) continue;
                if (!c1.getClass().equals(c2.getClass())) continue;
                if (c2.getState() != AnimalState.MOVING) continue;

                double dx = c1.getX() - c2.getX();
                double dy = (c1.getY() + c1.getImageView().getFitHeight() / 2.) -
                        (c2.getY() + c2.getImageView().getFitHeight() / 2.);

                if (Math.hypot(dx, dy) <= 150) {
                    Herd herd = new Herd(new ArrayList<>(List.of(c1, c2)));
                    carnivoreherds.add(herd);
                    break;
                }
            }
        }
    }
    private void checkToJoinHerds() {
        // --- Herbivores ---
        for (Herbivore herbivore : herbivores) {
            if (herbivore.getIsInAHerd()) continue;
            if (herbivore.getState() != AnimalState.MOVING) continue;

            for (Herd herd : herbivoreherds) {
                Animal leader = herd.getLeader();
                if (leader == null) continue;

                if (!herbivore.getClass().equals(leader.getClass())) continue;
                if (leader.getState() != AnimalState.MOVING) continue;

                double dx = herbivore.getX() - leader.getX();
                double dy = (herbivore.getY() + herbivore.getImageView().getFitHeight() / 2.) -
                        (leader.getY() + leader.getImageView().getFitHeight() / 2.);

                if (Math.hypot(dx, dy) <= 150) {
                    herd.addMember(herbivore);
                    break;
                }
            }
        }

        // --- Carnivores ---
        for (Carnivore carnivore : carnivores) {
            if (carnivore.getIsInAHerd()) continue;
            if (carnivore.getState() != AnimalState.MOVING) continue;

            for (Herd herd : carnivoreherds) {
                Animal leader = herd.getLeader();
                if (leader == null) continue;

                if (!carnivore.getClass().equals(leader.getClass())) continue;
                if (leader.getState() != AnimalState.MOVING) continue;

                double dx = carnivore.getX() - leader.getX();
                double dy = (carnivore.getY() + carnivore.getImageView().getFitHeight() / 2.) -
                        (leader.getY() + leader.getImageView().getFitHeight() / 2.);

                if (Math.hypot(dx, dy) <= 150) {
                    herd.addMember(carnivore);
                    break;
                }
            }
        }
    }
    private void cleanupSmallHerds(List<Herd> herds) {
        List<Herd> toRemove = new ArrayList<>();

        for (Herd herd : herds) {
            if (herd.getMemberCount() < 2) {
                for (Animal member : herd.getMembers()) {
                    member.setIsInAHerd(false);
                    member.setHerd(null);
                    member.transitionTo(IDLE);
                    member.setStateIconVisibility(false);
                    member.setRestingTimePassed(0.0);
                }
                toRemove.add(herd);
            }
        }

        herds.removeAll(toRemove);
    }


    private void updateHerdStates() {
        updateHerbivoreHerdStates();
        updateCarnivoreHerdStates();
    }
    private void updateHerbivoreHerdStates() {
        for (Herd herd : herbivoreherds) {
            herd.assignNewLeader();
            Animal leader = herd.getLeader();

            boolean isAnyManuallyPaused = herd.getMembers().stream().anyMatch(Animal::isManuallyPaused);
            if (isAnyManuallyPaused) continue;

            for (Animal animal : herd.getMembers()) {
                if(animal.isBeingEaten()) continue;
                handleHerbivoreInHerd(animal, leader);
            }
        }
        cleanupSmallHerds(herbivoreherds);
    }
    private void handleHerbivoreInHerd(Animal animal, Animal leader) {
        switch (animal.getState()) {
            case MOVING -> {
                if (animal == leader)
                    animal.moveTowardsTarget();
                else
                    animal.moveTowardsLeader(leader);
            }
            case RESTING -> animal.rest();
            case EATING -> animal.eat();
            case DRINKING -> animal.drink();
            case IDLE -> {
                if (animal == leader) {
                    ArrayList<Terrain> path;
                    if (animal.getThirst() < 25.0)
                        animal.preparePath(gameBoard.getTerrainGrid(), gameBoard.getLakeTerrains());
                    else if (animal.getHunger() < 25.0)
                        animal.preparePath(gameBoard.getTerrainGrid(), gameBoard.getPlantTerrains());
                    else
                        animal.preparePath(gameBoard.getTerrainGrid(), gameBoard.getGroundTerrains());

                    path = gameBoard.findPathDijkstra(animal.getStart(), animal.getTarget());
                    animal.setPath(path);
                }
                animal.transitionTo(MOVING);
            }
        }
    }
    private void updateCarnivoreHerdStates() {
        for (Herd herd : carnivoreherds) {
            herd.assignNewLeader();
            Animal leader = herd.getLeader();

            boolean isAnyManuallyPaused = herd.getMembers().stream().anyMatch(Animal::isManuallyPaused);
            if (isAnyManuallyPaused) continue;

            for (Animal animal : herd.getMembers()) {
                handleCarnivoreInHerd(animal, leader);
            }
        }
        cleanupSmallHerds(carnivoreherds);
    }
    private void handleCarnivoreInHerd(Animal animal, Animal leader) {
        Carnivore leaderCarnivore = (Carnivore) leader;
        System.out.println("Leader state: " + leader.getState());


        if (animal == leader) {
            // clearer naming

            switch (leaderCarnivore.getState()) {
                case MOVING -> leaderCarnivore.moveTowardsTarget();
                case RESTING -> leaderCarnivore.rest();
                case EATING -> {
                    leaderCarnivore.changeHunger(0.5);
                    leaderCarnivore.setStateIcon(hungerForCarnivore);
                    leaderCarnivore.setStateIconVisibility(true);

                    if (leaderCarnivore.getHunger() > 99.0) {
                        leaderCarnivore.setStateIconVisibility(false);
                        leaderCarnivore.transitionTo(IDLE);

                        Herbivore prey = leaderCarnivore.getPrey();
                        if (prey != null) {
                            prey.setBeingEaten(false);

                            if (prey.getIsInAHerd() && prey.getHerd() != null)
                                prey.getHerd().removeMember(prey);

                            gameController.removeHerbivore(prey);
                            herbivores.remove(prey);

                            leaderCarnivore.clearPrey();
                        }
                    }
                }
                case HUNTING -> leaderCarnivore.huntTarget();
                case DRINKING -> leaderCarnivore.drink();
                case IDLE -> {
                    if (leaderCarnivore.getThirst() < 25.0) {
                        leaderCarnivore.preparePath(gameBoard.getTerrainGrid(), gameBoard.getLakeTerrains());
                    } else if (leaderCarnivore.getHunger() < 25.0) {
                        leaderCarnivore.choosePrey(herbivores);
                        leaderCarnivore.transitionTo(HUNTING);
                        return;
                    } else {
                        leaderCarnivore.preparePath(gameBoard.getTerrainGrid(), gameBoard.getGroundTerrains());
                    }

                    ArrayList<Terrain> path = gameBoard.findPathDijkstra(leaderCarnivore.getStart(), leaderCarnivore.getTarget());
                    leaderCarnivore.setPath(path);
                    leaderCarnivore.transitionTo(MOVING);
                    System.out.println(path);
                }
            }
        }
        else {
            switch (animal.getState()) {
                case MOVING, HUNTING -> animal.moveTowardsLeader(leaderCarnivore);
                case RESTING -> animal.rest();
                case EATING -> animal.eat();
                case DRINKING -> animal.drink();
                case IDLE -> animal.transitionTo(MOVING);
            }
        }
        System.out.println("Carnivore thirst: " + animal.getThirst());
        System.out.println("Carnivore path size: " + leaderCarnivore.getPath().size());
    }

    //=========

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
            return Double.MAX_VALUE;
    }
    //==========

    //===MOVEMENT, STATE UPDATES
    private void updateAnimalStates() {
        for (Herbivore herbivore : herbivores) {
            herbivore.changeThirst(-0.03);
            herbivore.changeHunger(-0.05);

            if(herbivore.getIsInAHerd()) continue;
            if(herbivore.isManuallyPaused()) continue;
            if(herbivore.isBeingEaten()) continue;


            switch(herbivore.getState()){
                case MOVING -> herbivore.moveTowardsTarget();
                case RESTING -> herbivore.rest();
                case EATING -> herbivore.eat();
                case DRINKING -> herbivore.drink();
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

                    //System.out.println(herbivore.getPath());
                }
            }
        }

        for (Carnivore carnivore : carnivores){
            carnivore.changeThirst(-0.03);
            carnivore.changeHunger(-0.05);

            if(carnivore.getIsInAHerd()) continue;
            if(carnivore.isManuallyPaused()) continue;

            switch (carnivore.getState()){
                case MOVING -> carnivore.moveTowardsTarget();
                case RESTING -> carnivore.rest();
                case EATING -> {
                    carnivore.changeHunger(0.5);
                    carnivore.setStateIcon(hungerForCarnivore);
                    carnivore.setStateIconVisibility(true);

                    if (carnivore.getHunger() > 99.0) {
                        carnivore.setStateIconVisibility(false);
                        carnivore.transitionTo(IDLE);

                        Herbivore prey = carnivore.getPrey();
                        if (prey != null) {
                            prey.setBeingEaten(false);

                            // Remove from herd if needed
                            if (prey.getIsInAHerd() && prey.getHerd() != null) {
                                prey.getHerd().removeMember(prey);
                            }

                            // UI + game list
                            gameController.removeHerbivore(prey);
                            herbivores.remove(prey);

                            carnivore.clearPrey();
                        }
                    }

                }
                case DRINKING -> carnivore.drink();
                case HUNTING -> carnivore.huntTarget();
                case IDLE -> {
                    if (carnivore.getThirst() < 25.0) {
                        carnivore.preparePath(gameBoard.getTerrainGrid(), gameBoard.getLakeTerrains());
                    } else if (carnivore.getHunger() < 25.0) {
                        carnivore.choosePrey(herbivores);
                        carnivore.transitionTo(HUNTING);
                        return;
                    } else {
                        carnivore.preparePath(gameBoard.getTerrainGrid(), gameBoard.getGroundTerrains());
                    }

                    ArrayList<Terrain> path = gameBoard.findPathDijkstra(carnivore.getStart(), carnivore.getTarget());
                    carnivore.setPath(path);
                    carnivore.transitionTo(MOVING);
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




    //RANGER
    public void buyRanger(Ranger ranger){
        this.rangers.add(ranger);
    }



    //JEEP
    public void buyJeep() {
        startJeep();
    }
    public void startJeep() {
        Terrain start = gameBoard.getTerrainAt(5, 14);
        Terrain goal = gameBoard.getTerrainAt(15, 14);

        ArrayList<Terrain> roadPath = gameBoard.findRoadPathDijkstra(start, goal);

        if (roadPath == null || roadPath.size() < 2) {
            System.out.println(" No valid road path found between (5,14) and (15,14)");
            return;
        }

        jeepCount++;
        Jeep jeep = new Jeep(5 * 30 + 15, 14 * 30 + 15);
        jeep.setPath(roadPath);

        jeeps.add(jeep);
        gameBoard.getUiLayer().getChildren().add(jeep);
    }


    public void updateJeepPositions() {
        for (Jeep jeep : jeeps) {
            jeep.moveAlongPath();
        }
    }

    public void buyPlant(Landform placesPlant) {
        plants.add((Plant) placesPlant); //konverzió elég funky
        //System.out.println("plant added to list");
    }


    public void buyAnimal(Animal animal) {
        if (animal instanceof Herbivore herbivore) {
            this.herbivores.add(herbivore);
        } else if (animal instanceof Carnivore carnivore) {
            this.carnivores.add(carnivore);
        }
    }



    public void sellAnimal(Animal animal) {
        money += animal.getPrice();

        if (animal.getHerd() != null) {
            animal.getHerd().removeMember(animal);
        }

        if (animal instanceof Herbivore herbivore) {
            herbivores.remove(herbivore);
        } else if (animal instanceof Carnivore carnivore) {
            carnivores.remove(carnivore);
        }
    }



    public int winningDays;
    public Difficulty difficulty;

    public Pair<Integer, Integer> entrance;
    public Pair<Integer, Integer> exit;
    public ArrayList<Plant> plants;

    private Speed speed;
    private ArrayList<Integer> conditions;







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
