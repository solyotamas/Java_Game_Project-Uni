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

import java.util.*;

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
    private Timeline timeline;


    private double spentTime;
    private double firstConditionMetTime;
    private double winningHours;
    private double winningHoursNeeded;

    protected ArrayList<Carnivore> carnivores;
    protected ArrayList<Herbivore> herbivores;
    protected ArrayList<Tourist> tourists;
    private int jeepCount ;
    private int ticketPrice;
    private int money;
    private final Random rand = new Random();

    private boolean canCheckForLose = false;
    private boolean canCheckForWin = false;

    private ArrayList<Herd> carnivoreherds;
    private ArrayList<Herd> herbivoreherds;

    private ArrayList<Poacher> poachers;
    private ArrayList<Ranger> rangers;
    private ArrayList<Jeep> jeeps;
    public ArrayList<Road> roads;

    public Pair<Integer, Integer> entrance;
    public Pair<Integer, Integer> exit;

    public Difficulty difficulty;
    private Speed speed;
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
        gameBoard.generatePlants(rand.nextInt(10) + 10);

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
        lakes = new ArrayList<Lake>();


        entrance = new Pair<>(0, 0);
        exit = new Pair<>(0, 0);
        conditions = new ArrayList<Integer>();
        if (difficulty == EASY) {
            conditions.add(10000); // minimum pénz
            conditions.add(2);    // növényevők száma
            conditions.add(2);     // ragadozók száma
            conditions.add(1);    // turisták száma havonta
            winningHoursNeeded = 24; // 3 hónapig tartani
            money = 15000;
            System.out.println("A nyeréshez szükséges legalább 10 000$-t, 2 növényevőt, 2 ragadozót és 1 turistát 1 napig megtartanod.");
        } else if (difficulty == MEDIUM) {
            conditions.add(20000);
            conditions.add(3);
            conditions.add(3);
            conditions.add(2);
            winningHoursNeeded = 48;
            money = 10000;
            System.out.println("A nyeréshez szükséges legalább 20 000$-t, 3 növényevőt, 3 ragadozót és 2 turistát 2 napig megtartanod.");
        } else { // HARD
            conditions.add(30000);
            conditions.add(4);
            conditions.add(4);
            conditions.add(3);
            winningHoursNeeded = 72;
            money = 5000;
            System.out.println("A nyeréshez szükséges legalább 30 000$-t, 4 növényevőt, 4 ragadozót és 3 turistát 3 napig megtartanod.");
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
                updateJeepPositions();

                //== VISUALS
                sortUiLayer();

                //LOG
                //logWhatever();

                //== TOURISTS
                maybeSpawnTourist();

                if (gameOver()) {
                    if (money == 0) {
                        gameController.setReasonOfDeathText("Your safari park has gone bankrupt.");

                    } else {
                        gameController.setReasonOfDeathText("All of your animals have died.");
                    }
                    gameController.openLosePane();
                    timeline.stop();
                } else if (gameWon()) {
                    gameController.openWinPane();
                    timeline.stop();
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

    public void logWhatever(){
        System.out.println(herbivoreherds.size());
    }

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

                            gameController.removeAnimal(prey);
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
            return jeep.getDepth();
        else
            return Double.MAX_VALUE;
    }
    // =====


    // ==== UPDATE STATES
    // Animal
    private void updateAnimalStates() {
        removeOldAnimals(herbivores, spentTime);
        removeOldAnimals(carnivores, spentTime);

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
                            gameController.removeAnimal(prey);
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
    // --- Animal

    // Human
    private Carnivore selectedCarnivore = null;
    private void updateHumanStates() {

        // Ranger
        List<Ranger> toRemoveRangers = new ArrayList<>();

        for (Ranger ranger : rangers) {
            switch (ranger.getState()){
                case MOVING -> {
                    // Choosing new prey while moving
                    if (selectedCarnivore != null) {
                        ranger.transitionTo(HumanState.CAPTURING);
                    } else {
                        ranger.moveTowardsTarget(); // Moving towards prey
                    }
                }
                case RESTING -> {
                    // Choosing new prey while resting
                    if (selectedCarnivore != null) {
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
                    for (Carnivore carnivore : carnivores) {
                        carnivore.setStyle("");
                    }

                    // Checking if animal hasn't been sold or hasn't died while capturing
                    if (selectedCarnivore != null && carnivores.contains(selectedCarnivore)) {
                        ranger.choosePrey(selectedCarnivore);
                        ranger.huntTarget();
                    } else {
                        selectedCarnivore = null;
                        ranger.transitionTo(HumanState.RESTING);
                    }

                }
                case CAPTURED -> {
                    ranger.transitionTo(HumanState.RESTING);
                    selectedCarnivore = null;
                    gameController.removeAnimal(ranger.getPrey());
                    carnivores.remove(ranger.getPrey());
                }
            }

            if (ranger.isDueForPayment(spentTime)) {
                if (money >= 5000) {
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

        // Poacher
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
        // --- Poacher

        // Tourist
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
        // --- Tourist
    }
    // --- Human
    // =====

    // ==== RANGERS
    public void buyRanger(Ranger ranger){
        ranger.setLastPaidHour(spentTime);
        this.rangers.add(ranger);
        money = Math.max(0, money - 5000);
    }

    public void payRanger(Ranger ranger) {
        if (money >= 5000) {
            money -= 5000;
            ranger.setLastPaidHour(spentTime);
        } else {
            unemployRanger(ranger);
        }
    }
    // =====

    //JEEP
/*    public void buyJeep() {
        money = Math.max(0, money - 5000);
        jeepCount++;
    }*/
    public void buyJeep() {
        startJeep();
    }

    /*    public void startJeep() {
        if (tourists.size() >= 4 && jeepCount >= 1) {
            for (int i = 0; i < 4; i++) {
                tourists.removeLast();
            }
            Jeep jeep = new Jeep(150, 0);
            // jeep = new Jeep(1770, 900);
            jeeps.add(jeep);
            gameBoard.getUiLayer().getChildren().add(jeep);
            gameController.updateDisplay(spentTime, carnivores.size(), herbivores.size(), jeepCount, tourists.size(), ticketPrice, money);
        }
    }*/
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

    public void unemployRanger(Ranger ranger) {

        rangers.remove(ranger);
        gameBoard.getUiLayer().getChildren().remove(ranger);
        System.out.println(rangers.size());
    }

    public void choosePreyForRanger() {
        Pane root = gameBoard.getUiLayer();

        for (Carnivore carnivore : carnivores) {
            carnivore.setStyle("-fx-border-color: red; -fx-border-width: 2;");
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

                    System.out.println("Kiválasztott új célpont: " + carnivore);
                    selectedCarnivore = carnivore;
                    root.setOnMouseClicked(null);
                    return;
                }
            }

            System.out.println("Nem érvényes célpont.");
            selectedCarnivore = null;
            root.setOnMouseClicked(null);
        });
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
        animal.setBornAt(spentTime);
        canCheckForLose = true;
    }

    public void sellAnimal(Animal animal) {
        money += animal.getSellPrice();

        if (animal.getHerd() != null) {
            animal.getHerd().removeMember(animal);
        }

        if (animal instanceof Herbivore herbivore) {
            herbivores.remove(herbivore);
        } else if (animal instanceof Carnivore carnivore) {
            carnivores.remove(carnivore);
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
            //System.out.println("Conditions met! Start counting!");
        }

        // If any condition fails
        if (canCheckForWin && !checkConditions()) {
            winningHours = 0;
            canCheckForWin = false;
            //System.out.println("Conditions fail!");
        }

        if (canCheckForWin && checkConditions()) {
            if ((spentTime - firstConditionMetTime) >= winningHoursNeeded) {
                return true;
            } else {
                winningHours = spentTime - firstConditionMetTime;
                //System.out.println("Conditions met! Winning hours: " + winningHours);
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
    // =====

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
}
