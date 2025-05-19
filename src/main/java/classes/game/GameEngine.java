package classes.game;

import classes.Difficulty;
import classes.JeepState;
import classes.entities.additions.InfoWindowAnimal;
import classes.entities.additions.InfoWindowRanger;
import classes.entities.human.*;
import classes.entities.animals.*;
import classes.Jeep;
import classes.landforms.*;
import classes.controllers.GameController;
import classes.landforms.plants.Plant;
import classes.terrains.Terrain;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import javafx.util.Pair;

import java.util.*;

import static classes.Difficulty.EASY;
import static classes.Difficulty.MEDIUM;
import static classes.entities.animals.AnimalState.*;

public class GameEngine {
    private GameController gameController;
    private GameBoard gameBoard;
    private Timeline timeline;


    private double spentTime;
    private double firstConditionMetTime;
    private double winningHours;
    private double winningHoursNeeded;

    protected ArrayList<Carnivore> carnivores;
    protected ArrayList<Herbivore> herbivores;
    protected ArrayList<Tourist> tourists;
    private int jeepCount ;
    private int ticketPrice = 100;
    private int money;
    private final Random rand = new Random();

    private boolean canCheckForLose = false;
    private boolean canCheckForWin = false;

    private ArrayList<Herd> carnivoreherds;
    private ArrayList<Herd> herbivoreherds;

    private ArrayList<Ranger> rangers;
    private ArrayList<Jeep> jeeps;
    public ArrayList<Road> roads;

    public Pair<Integer, Integer> entrance;
    public Pair<Integer, Integer> exit;

    public Difficulty difficulty;
    private ArrayList<Integer> conditions;

    public ArrayList<Plant> plants;
    public ArrayList<Lake> lakes;


    //need
    private final Image hungerForCarnivore = new Image(GameEngine.class.getResource("/images/hunger.png").toExternalForm());

    public GameEngine(GameController gameController, Difficulty difficulty, Pane terrainLayer, Pane uiLayer) {
        this.gameController = gameController;
        this.difficulty = difficulty;

        this.gameBoard = new GameBoard(terrainLayer, uiLayer);
        gameBoard.setupGroundBoard();
        gameBoard.generatePlants(rand.nextInt(1) + 1);

        carnivores = new ArrayList<Carnivore>();
        herbivores = new ArrayList<Herbivore>();
        tourists = new ArrayList<Tourist>();

        carnivoreherds = new ArrayList<>();
        herbivoreherds = new ArrayList<>();

        rangers = new ArrayList<Ranger>();
        jeeps = new ArrayList<Jeep>();
        roads = new ArrayList<Road>();
        plants = new ArrayList<Plant>();
        lakes = new ArrayList<Lake>();


        entrance = new Pair<>(0, 0);
        exit = new Pair<>(0, 0);
        conditions = new ArrayList<Integer>();
        if (difficulty == EASY) {
            conditions.add(300000); // money
            conditions.add(5);    // herbivores
            conditions.add(5);     // carnivores
            conditions.add(3);    // tourists
            winningHoursNeeded = 720; // one month
            money = 25000;
            System.out.println("To win, you need to keep at least $300,000, 5 herbivores, 5 carnivores, and 3 tourists for 30 days.");
        } else if (difficulty == MEDIUM) {
            conditions.add(500000);
            conditions.add(8);
            conditions.add(8);
            conditions.add(6);
            winningHoursNeeded = 1440;
            money = 20000;
            System.out.println("To win, you need to keep at least $500,000, 8 herbivores, 8 carnivores, and 6 tourists for 60 days.");
        } else { // HARD
            conditions.add(1000000);
            conditions.add(10);
            conditions.add(10);
            conditions.add(8);
            winningHoursNeeded = 2160;
            money = 15000;
            System.out.println("To win, you need to keep at least $1,000,000, 10 herbivores, 10 carnivores, and 8 tourists for 90 days.");
        }
    }

    public void gameLoop() {
        savePlants();

        timeline = new Timeline(
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
                checkIfJeepCanStart();
                updateJeepPositions();


                //== VISUALS
                sortUiLayer();

                //LOG
                //logWhatever();

                //== TOURISTS
                maybeSpawnTourist();

                // UI sync, Display things
                spentTime += 0.05;
                gameController.updateDisplay(spentTime, carnivores.size(), herbivores.size(), jeepCount, tourists.size(), ticketPrice, money);

                if (gameOver()) {
                    if (money == 0) {
                        gameController.setReasonOfDeathText("Your safari park has gone bankrupt.");

                    } else {
                        gameController.setReasonOfDeathText("All of your animals have died.");
                    }
                    gameController.openLosePane();
                    stop();
                } else if (gameWon()) {
                    gameController.openWinPane();
                    stop();
                }

            })
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }


    // ==== SPAWNING TOURISTS
    private void maybeSpawnTourist() {
        double chancePerSecond = 0.03 * (herbivores.size() + (carnivores.size()) * 2) + getTotalUniqueAnimalsSeenByJeeps() / 100.;
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

    // ==== HERDS
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
                Terrain terrainUnder = gameBoard.getTerrainAtDouble(animal.getX(), animal.getY());
                if (animal == leader)
                    animal.moveTowardsTarget(terrainUnder);
                else
                    animal.moveTowardsLeader(leader, terrainUnder);
            }
            case RESTING -> animal.rest();
            case EATING -> {
                animal.eat();

                Terrain target = animal.getTarget();
                if (target != null) {
                    Landform landform = target.getLandform();
                    if (landform != null && landform instanceof Plant plant && plant.isDepleted()) {
                        plants.remove(plant); // TODO BUGFIX still in desired plants ???
                        target.setLandform(null);
                        gameBoard.getUiLayer().getChildren().remove(plant);
                    }
                }
            }
            case DRINKING -> animal.drink();
            case IDLE -> {
                if (animal == leader) {
                    ArrayList<Terrain> path;
                    if (animal.getThirst() < 25.0)
                        animal.preparePath(gameBoard.getTerrainGrid(), gameBoard.getLakeTerrains());
                    else if (animal.getHunger() < 25.0)
                        if (!gameBoard.getPlantTerrains().isEmpty()) {
                            animal.preparePath(gameBoard.getTerrainGrid(), gameBoard.getPlantTerrains());
                            animal.setStarving(false);
                        } else {
                            animal.preparePath(gameBoard.getTerrainGrid(), gameBoard.getGroundTerrains());
                            animal.setStarving(true);
                        }
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

        Terrain terrainUnder = gameBoard.getTerrainAtDouble(leaderCarnivore.getX(), leaderCarnivore.getY());

        if (animal == leader) {
            // clearer naming

            switch (leaderCarnivore.getState()) {
                case MOVING -> leaderCarnivore.moveTowardsTarget(terrainUnder);
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

                            gameController.removeAnimal(prey);
                            herbivores.remove(prey);

                            updateTicketPrice();
                            leaderCarnivore.clearPrey();
                        }
                    }
                }
                case HUNTING -> leaderCarnivore.huntTarget(terrainUnder);
                case DRINKING -> leaderCarnivore.drink();
                case IDLE -> {
                    if (leaderCarnivore.getThirst() < 25.0) {
                        leaderCarnivore.preparePath(gameBoard.getTerrainGrid(), gameBoard.getLakeTerrains());
                    } else if (leaderCarnivore.getHunger() < 25.0) {
                        leaderCarnivore.choosePrey(leaderCarnivore, herbivores);
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
                case MOVING, HUNTING -> animal.moveTowardsLeader(leaderCarnivore, terrainUnder);
                case RESTING -> animal.rest();
                case EATING -> animal.eat();
                case DRINKING -> animal.drink();
                case IDLE -> animal.transitionTo(MOVING);
            }
        }
    }

    // ======

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
            return jeep.getY();
        else
            return Double.MAX_VALUE;
    }
    // =====


    // ==== UPDATE STATES
    // Animal
    private void updateAnimalStates() {
        removeOldAnimals(herbivores, spentTime);
        removeOldAnimals(carnivores, spentTime);
        removeStarvedAnimals(herbivores);
        removeStarvedAnimals(carnivores);

        for (Herbivore herbivore : herbivores) {
            herbivore.changeThirst(-0.03);
            herbivore.changeHunger(-0.05);
            herbivore.incrementStarvingTime(0.05);

            if(herbivore.getIsInAHerd()) continue;
            if(herbivore.isManuallyPaused()) continue;
            if(herbivore.isBeingEaten()) continue;

            Terrain terrainUnder = gameBoard.getTerrainAtDouble(herbivore.getX(), herbivore.getY());

            switch(herbivore.getState()){
                case MOVING -> herbivore.moveTowardsTarget(terrainUnder);
                case RESTING -> herbivore.rest();
                case EATING -> {
                    herbivore.eat();

                    Terrain target = herbivore.getTarget();
                    if (target != null) {
                        Landform landform = target.getLandform();
                        if (landform != null && landform instanceof Plant plant && plant.isDepleted()) {
                            plants.remove(plant);
                            target.setLandform(null);
                            gameBoard.getUiLayer().getChildren().remove(plant);
                        }
                    }
                }
                case DRINKING -> herbivore.drink();
                case IDLE -> {
                    if (herbivore.getThirst() < 25.0) {
                        herbivore.preparePath(gameBoard.getTerrainGrid(), gameBoard.getLakeTerrains());
                    } else if (herbivore.getHunger() < 25.0) {
                        if (!gameBoard.getPlantTerrains().isEmpty()) {
                            herbivore.preparePath(gameBoard.getTerrainGrid(), gameBoard.getPlantTerrains());
                            herbivore.setStarving(false);
                        } else {
                            herbivore.preparePath(gameBoard.getTerrainGrid(), gameBoard.getGroundTerrains());
                            herbivore.setStarving(true);
                        }
                    } else {
                        herbivore.preparePath(gameBoard.getTerrainGrid(), gameBoard.getGroundTerrains());
                    }
                    ArrayList<Terrain> path = gameBoard.findPathDijkstra(herbivore.getStart(), herbivore.getTarget());
                    herbivore.setPath(path);
                    herbivore.transitionTo(MOVING);
                }
            }
        }

        for (Carnivore carnivore : carnivores){
            carnivore.changeThirst(-0.03);
            carnivore.changeHunger(-0.05);
            carnivore.incrementStarvingTime(0.05);

            if(carnivore.getIsInAHerd()) continue;
            if(carnivore.isManuallyPaused()) continue;

            Terrain terrainUnder = gameBoard.getTerrainAtDouble(carnivore.getX(), carnivore.getY());

            switch (carnivore.getState()){
                case MOVING -> carnivore.moveTowardsTarget(terrainUnder);
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
                            gameController.removeAnimal(prey);
                            herbivores.remove(prey);

                            updateTicketPrice();
                            carnivore.clearPrey();
                        }
                    }

                }
                case DRINKING -> carnivore.drink();
                case HUNTING -> carnivore.huntTarget(terrainUnder);
                case IDLE -> {
                    if (carnivore.getThirst() < 25.0) {
                        carnivore.preparePath(gameBoard.getTerrainGrid(), gameBoard.getLakeTerrains());
                    } else if (carnivore.getHunger() < 25.0) {
                        carnivore.choosePrey(carnivore, herbivores);
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

    private void removeOldAnimals(List<? extends Animal> animals, double spentTime) {
        Iterator<? extends Animal> it = animals.iterator();
        List<Animal> animalsToRemove = new ArrayList<>();

        while (it.hasNext()) {
            Animal animal = it.next();
            if (animal.oldEnoughToDie(spentTime)) {
                animalsToRemove.add(animal);
            }
        }

        for (Animal animal : animalsToRemove) {
            animals.remove(animal);
            gameBoard.getUiLayer().getChildren().remove(animal);
        }
    }

    private void removeStarvedAnimals(List<? extends Animal> animals) {
        Iterator<? extends Animal> it = animals.iterator();
        List<Animal> animalsToRemove = new ArrayList<>();

        while (it.hasNext()) {
            Animal animal = it.next();
            if (animal.diedOfStarvation()) {
                animalsToRemove.add(animal);
            }
        }

        for (Animal animal : animalsToRemove) {
            if (animal.getIsInAHerd() && animal.getHerd() != null) {
                animal.getHerd().removeMember(animal);
            }

            animals.remove(animal);
            gameBoard.getUiLayer().getChildren().remove(animal);
            updateTicketPrice();
        }
    }
    // --- Animal

    // Human
    private void updateHumanStates() {

        // Ranger
        List<Ranger> toRemoveRangers = new ArrayList<>();

        for (Ranger ranger : rangers) {
            switch (ranger.getState()){
                case MOVING -> {
                    // Choosing new prey while moving
                    if (ranger.getPrey() != null) {
                        ranger.transitionTo(HumanState.CAPTURING);
                    } else {
                        ranger.moveTowardsTarget(); // Moving towards prey
                    }
                }
                case RESTING -> {
                    // Choosing new prey while resting
                    if (ranger.getPrey() != null) {
                        ranger.transitionTo(HumanState.CAPTURING);
                    } else {
                        ranger.rest(); // Resting
                    }
                }
                case PAUSED -> {}
                case IDLE -> {
                    ranger.pickNewTarget();
                    ranger.transitionTo(HumanState.MOVING);
                }
                case CAPTURING -> {
                    // Checking if animal hasn't been sold or hasn't died while capturing
                    if (ranger.getPrey() != null && carnivores.contains(ranger.getPrey())) {
                        ranger.choosePrey(ranger.getPrey());
                        ranger.huntTarget();
                    } else {
                        ranger.setPrey(null);
                        ranger.transitionTo(HumanState.RESTING);
                    }

                }
                case CAPTURED -> {
                    ranger.transitionTo(HumanState.RESTING);
                    gameController.removeAnimal(ranger.getPrey());
                    carnivores.remove(ranger.getPrey());
                    updateTicketPrice();
                    ranger.setPrey(null);
                }
            }

            if (ranger.isDueForPayment(spentTime)) {
                if (money >= ranger.getPrice()) {
                    payRanger(ranger);
                } else {
                    toRemoveRangers.add(ranger);
                }
            }

        }

        for (Ranger ranger : toRemoveRangers) {
            unemployRanger(ranger);
        }
        // --- Ranger

        // Tourist
        List<Tourist> toRemove = new ArrayList<>();
        for (Tourist tourist : tourists){
            tourist.changeVisitDuration(1);

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
        // --- Tourist
    }
    // --- Human
    // =====

    // ==== RANGERS
    public void buyRanger(Ranger ranger){
        ranger.setLastPaidHour(spentTime);
        this.rangers.add(ranger);
        money = Math.max(0, money - ranger.getPrice());
    }

    public void payRanger(Ranger ranger) {
        if (money >= ranger.getPrice()) {
            money -= ranger.getPrice();
            ranger.setLastPaidHour(spentTime);
        } else {
            unemployRanger(ranger);
        }
    }
    // =====

    //JEEP
    public void buyJeep() {
        Jeep jeep = null;

        int firstTry = rand.nextInt(2);
        int secondTry = 1 - firstTry;

        int[][] positions = {
                {5, 0},       // entrance
                {58, 30}      // exit
        };

        for (int i = 0; i < 2; i++) {
            int side = (i == 0) ? firstTry : secondTry;
            int x = positions[side][0];
            int y = positions[side][1];

            if (gameBoard.getTerrainAt(x, y).getLandform() instanceof Road) {
                jeep = new Jeep(x * 30 + 15, y * 30 + 15);

                if (side == 1) {
                    jeep.setImageView(jeep.getJeepLeft());
                }

                money = Math.max(0, money - 4500);
                break;
            }
        }

        if (jeep != null) {
            jeep.transitionTo(JeepState.IDLE);
            jeepCount++;
            jeeps.add(jeep);
            gameBoard.getUiLayer().getChildren().add(jeep);
        }
    }

    public void checkIfJeepCanStart(){

        int movingJeeps = 0;
        for (Jeep jeep : jeeps) {
            if (jeep.getStatus() == JeepState.MOVING) {
                movingJeeps += 1;
            }
        }

        if(movingJeeps == 0)
            movingJeeps = 1;

        if (tourists.size() >= 4 * movingJeeps && !jeeps.isEmpty()){
            List<Jeep> idleJeeps = new ArrayList<>();

            for (Jeep jeep : jeeps) {
                if (jeep.getStatus() == JeepState.IDLE) {
                    idleJeeps.add(jeep);
                }
            }

            if(idleJeeps.size() > 0){
                Jeep selectedJeep = idleJeeps.get(rand.nextInt(idleJeeps.size()));
                start(selectedJeep);
            }

        }
    }

    public void start(Jeep jeep) {
        jeep.transitionTo(JeepState.MOVING);
        int tileY = (int)(jeep.getY()) / 30;

        if(tileY == 0){
            startJeep(jeep, 5, 0, 58, 30);
        }else
            startJeep(jeep, 58, 30, 5, 0);

    }

    public void startJeep(Jeep jeep, int startX, int startY, int endX, int endY){
        Terrain start = gameBoard.getTerrainAt(startX, startY);
        Terrain goal = gameBoard.getTerrainAt(endX, endY);

        ArrayList<Terrain> roadPath = gameBoard.findRoadPathDijkstra(start, goal);


        if (roadPath == null || roadPath.size() < 2) {
            //System.out.println("No valid road path found between (" + startX + "," + startY + ") and (" + endX + "," + endY + ")");
            return;
        }

        jeep.setPath(roadPath);
        money += ticketPrice;
    }

    public int getTotalUniqueAnimalsSeenByJeeps() {
        Set<Class<? extends Animal>> uniqueSpecies = new HashSet<>();

        for (Jeep jeep : jeeps) {
            uniqueSpecies.addAll(jeep.getSpeciesSeen());
        }

        return uniqueSpecies.size();
    }


    public void updateJeepPositions() {
        for (Jeep jeep : jeeps) {
            if (jeep.getStatus() == JeepState.MOVING) {
                checkJeepProximityToAnimals(jeep);
                jeep.moveAlongPath();
            }
        }
    }

    private void checkJeepProximityToAnimals(Jeep jeep) {
        double radius = 150.0; //5x5 circle radius
        double jeepX = jeep.getX();
        double jeepY = jeep.getY();

        for (Animal animal : herbivores) {
            if (isWithinRadius(jeepX, jeepY, animal.getX(), animal.getY(), radius)) {
                jeep.getSpeciesSeen().add(animal.getClass());
            }
        }

        for (Animal animal : carnivores) {
            if (isWithinRadius(jeepX, jeepY, animal.getX(), animal.getY(), radius)) {
                jeep.getSpeciesSeen().add(animal.getClass());
            }
        }
    }

    private boolean isWithinRadius(double x1, double y1, double x2, double y2, double radius) {
        double dx = x1 - x2;
        double dy = y1 - y2;
        return (dx * dx + dy * dy) <= (radius * radius);
    }


    public void unemployRanger(Ranger ranger) {

        rangers.remove(ranger);
        gameBoard.getUiLayer().getChildren().remove(ranger);
        System.out.println(rangers.size());
    }

    public void choosePreyForRanger(Ranger ranger) {
        Pane root = gameBoard.getUiLayer();

        for (Carnivore carnivore : carnivores) {
            highlightCarnivore(carnivore);
        }

        root.setOnMouseClicked(event -> {
            double clickX = event.getX();
            double clickY = event.getY();

            for (Carnivore carnivore : carnivores) {
                Pane pane = carnivore;
                double layoutX = pane.getLayoutX();
                double layoutY = pane.getLayoutY();
                double width = pane.getWidth();
                double height = pane.getHeight();

                if (clickX >= layoutX && clickX <= layoutX + width &&
                        clickY >= layoutY && clickY <= layoutY + height) {

                    System.out.println("Chosen new target: " + carnivore);
                    ranger.setPrey(carnivore);
                    root.setOnMouseClicked(null);
                    gameController.closeRangerWindow(ranger);
                    return;
                }
            }

            System.out.println("Not a valid target.");
            ranger.setPrey(null);
            root.setOnMouseClicked(null);
        });
    }

    // Highlighting targetable carnivores
    private void highlightCarnivore(Carnivore carnivore) {
        DropShadow glow = new DropShadow();
        glow.setColor(Color.GREENYELLOW);
        glow.setRadius(20);
        glow.setSpread(0.4);

        carnivore.setEffect(glow);

        Timeline pulse = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(glow.radiusProperty(), 10)),
                new KeyFrame(Duration.seconds(0.5), new KeyValue(glow.radiusProperty(), 20))
        );
        pulse.setCycleCount(Animation.INDEFINITE);
        pulse.setAutoReverse(true);
        pulse.play();

        carnivore.getProperties().put("pulse", pulse);
    }

    public void unhighlightCarnivore(Carnivore carnivore) {
        carnivore.setEffect(null);
        Object pulseObj = carnivore.getProperties().remove("pulse");

        if (pulseObj instanceof Timeline pulse) {
            pulse.stop();
        }
    }

    public void clearAllCarnivoreHighlights() {
        for (Carnivore carnivore : carnivores) {
            unhighlightCarnivore(carnivore);
        }
    }
    // =====

    // ==== SELLING, BUYING
    public void buyPlant(Plant plant) {
        plants.add(plant);
        money = Math.max(0, money - plant.getPrice());
    }

    public void buyLake(Lake lake) {
        lakes.add(lake);
        money = Math.max(0, money - lake.getPrice());
    }

    public void buyRoad(Road road) {
        roads.add(road);
        money = Math.max(0, money - road.getPrice());
    }


    public void buyAnimal(Animal animal) {
        if (animal instanceof Herbivore herbivore) {
            this.herbivores.add(herbivore);
        } else if (animal instanceof Carnivore carnivore) {
            this.carnivores.add(carnivore);
        }
        money = Math.max(0, money - animal.getPrice());
        updateTicketPrice();
        animal.setBornAt(spentTime);
        canCheckForLose = true;
    }

    public void updateTicketPrice() {
        int ticketPrice = 100;
        for (Carnivore carnivore : carnivores) {
            ticketPrice += carnivore.getPrice() / 10;
        }
        for (Herbivore herbivore : herbivores) {
            ticketPrice += herbivore.getPrice() / 10;
        }
        this.ticketPrice = ticketPrice;
    }

    public void sellAnimal(Animal animal) {
        money += animal.getSellPrice();

        if (animal.getHerd() != null) {
            animal.getHerd().removeMember(animal);
        }

        if (animal instanceof Herbivore herbivore) {
            herbivores.remove(herbivore);
            updateTicketPrice();
        } else if (animal instanceof Carnivore carnivore) {
            carnivores.remove(carnivore);
            updateTicketPrice();
        }
    }
    // =====

    // ==== GAME WINNING, LOSING
    public boolean gameOver() {
        return (canCheckForLose && (herbivores.isEmpty() && carnivores.isEmpty())) || money <= 0;
    }

    public boolean gameWon() {
        // First time reaching the required conditions
        if (!canCheckForWin && checkConditions()) {
            canCheckForWin = true;
            firstConditionMetTime = spentTime;
        }

        // If any condition fails
        if (canCheckForWin && !checkConditions()) {
            winningHours = 0;
            canCheckForWin = false;
        }

        if (canCheckForWin && checkConditions()) {
            if ((spentTime - firstConditionMetTime) >= winningHoursNeeded) {
                return true;
            } else {
                winningHours = spentTime - firstConditionMetTime;
            }
        }

        return false;
    }

    private boolean checkConditions() {
        return money >= conditions.get(0)
                && herbivores.size() >= conditions.get(1)
                && carnivores.size() >= conditions.get(2)
                && tourists.size() >= conditions.get(3);
    }

    public void stop() {
        if (timeline != null) {
            timeline.stop();
        }

        carnivores.clear();
        herbivores.clear();
        rangers.clear();
        tourists.clear();
        jeeps.clear();
        plants.clear();

        if (gameBoard != null && gameBoard.getUiLayer() != null) {
            gameBoard.getUiLayer().getChildren().clear();
        }
    }
    // =====

    // ==== GAME SPEED
    public void setGameSpeed(int gameSpeed) {
        if (timeline != null) {
            timeline.setRate(gameSpeed);
        }
    }
    // =====

    public GameBoard getGameBoard(){
        return this.gameBoard;
    }

    public void savePlants() {
        for (Node node : gameBoard.getUiLayer().getChildren()) {
            if (node instanceof Plant) {
                plants.add((Plant) node);
            }
        }
    }
}
