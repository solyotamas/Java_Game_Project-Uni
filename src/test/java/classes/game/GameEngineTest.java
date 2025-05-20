package classes.game;

import classes.Difficulty;
import classes.Jeep;
import classes.controllers.GameController;
import classes.entities.animals.carnivores.Lion;
import classes.entities.animals.herbivores.Elephant;
import classes.entities.human.Ranger;
//import classes.map.GameMap;
import classes.entities.human.Tourist;
import classes.landforms.Lake;
import classes.landforms.Road;
import classes.landforms.plants.Tree;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.layout.Pane;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class GameEngineTest {

    private GameEngine gameEngineE;
    private GameEngine gameEngineM;
    private GameEngine gameEngineH;

    @BeforeAll
    public static void initJavaFX() {
        try {
            Platform.startup(() -> {
            });
        } catch (IllegalStateException e) {
            // JavaFX már fut
        }
    }

    @BeforeEach
    void setUp() {
        gameEngineE = new GameEngine(null, Difficulty.EASY, null, null);
        gameEngineM = new GameEngine(null, Difficulty.MEDIUM, null, null);
        gameEngineH = new GameEngine(null, Difficulty.HARD, null, null);
    }

    @Test
    void testStartGameInitializesDifficulty() {
        ArrayList<Integer> condE = new ArrayList<Integer>();
        condE.add(40000); // money
        condE.add(5);    // herbivores
        condE.add(5);     // carnivores
        condE.add(15);    // tourists
        double winningHoursNeeded = 720; // one month
        int money = 25000;

        assertEquals(condE, gameEngineE.getConditions());


        ArrayList<Integer> condM = new ArrayList<Integer>();
        condM.add(700000);
        condM.add(8);
        condM.add(8);
        condM.add(25);
        winningHoursNeeded = 1440;
        money = 20000;

        assertEquals(condM, gameEngineM.getConditions());


        ArrayList<Integer> condH = new ArrayList<Integer>();
        condH.add(100000);
        condH.add(10);
        condH.add(10);
        condH.add(40);
        winningHoursNeeded = 2160;
        money = 15000;

        assertEquals(condH, gameEngineH.getConditions());
    }


    @Test
    void testGameOverWhenNoAnimalsAndCanCheckForLose() {
        gameEngineE.getHerbivores().clear();
        gameEngineE.getCarnivores().clear();
        gameEngineE.setCanCheckForLose(true);
        gameEngineE.setMoney(10000); // pénz van, csak állat nincs

        assertTrue(gameEngineE.gameOver());
    }

    @Test
    void testGameOverWhenMoneyZero() {
        gameEngineE.setMoney(0); // pénz elfogyott
        gameEngineE.setCanCheckForLose(false); // állatok számát most ne nézzük

        assertTrue(gameEngineE.gameOver());
    }

    @Test
    void testGameNotOver() {
        gameEngineE.setMoney(10000);
        gameEngineE.setCanCheckForLose(true);
        gameEngineE.getHerbivores().add(new Elephant(0, 0, false));
        gameEngineE.getCarnivores().add(new Lion(1, 1, false));

        assertFalse(gameEngineE.gameOver());
    }

    @Test
    void testGameWonAfterSustainedConditions() {
        gameEngineE.setSpentTime(0);
        gameEngineE.setCanCheckForWin(true);
        gameEngineE.setFirstConditionMetTime(0);

        gameEngineE.checkConditions();
        assertFalse(gameEngineE.gameWon());

        gameEngineE.setWinningHours(721);
        gameEngineE.setMoney(40001);
        for (int i = 0; i < 15; i++) {
            gameEngineE.herbivores.add(new Elephant(0, 0, false));
            gameEngineE.carnivores.add(new Lion(1, 1, false));
            gameEngineE.tourists.add(new Tourist(2, 2, 0));
        }
        gameEngineE.setCanCheckForWin(true);
        gameEngineE.setSpentTime(1000);
        gameEngineE.checkConditions();

//        System.out.println(gameEngineE.getSpentTime() + " - " + gameEngineE.getFirstConditionMetTime() + " >= " + gameEngineE.getWinningHoursNeeded());
//        System.out.println(gameEngineE.getSpentTime() + " , " + gameEngineE.getFirstConditionMetTime());
//        System.out.println(gameEngineE.getCanCheckForWin() + " , " + gameEngineE.checkConditions());
        assertTrue(gameEngineE.gameWon());

    }

    @Test
    void testGameWonResetsIfConditionFails() {
        gameEngineE.setSpentTime(0);
        //gameEngineE.setWinningHoursNeeded(10);

        gameEngineE.setMoney(40001);
        for (int i = 0; i < 15; i++) {
            gameEngineE.herbivores.add(new Elephant(0, 0, false));
            gameEngineE.carnivores.add(new Lion(1, 1, false));
            gameEngineE.tourists.add(new Tourist(2, 2, 0));
        }

        //assertFalse(gameEngineE.gameWon());

        gameEngineE.setSpentTime(50);
        assertFalse(gameEngineE.gameWon());

        // rossz feltételek
        gameEngineE.herbivores.clear();
        assertFalse(gameEngineE.gameWon()); // canCheckForWin ugrik

        for (int i = 0; i < 15; i++) {
            gameEngineE.herbivores.add(new Elephant(0, 0, false));
        }
        //teszteli hogy a canCheckFroWin átáll e
        gameEngineE.setSpentTime(20);
        assertFalse(gameEngineE.gameWon());

        gameEngineE.setSpentTime(1000);
        assertTrue(gameEngineE.gameWon());
    }

    @Test
    void testStopMethodClearsGameState() {
        Timeline timeline = new Timeline();
        gameEngineE.setTimeline(timeline);

        gameEngineE.carnivores.add(new Lion(0, 0, false));
        gameEngineE.herbivores.add(new Elephant(1, 1, false));
        gameEngineE.rangers.add(new Ranger(2, 2));
        gameEngineE.tourists.add(new Tourist(3, 3, 0));
        gameEngineE.jeeps.add(new Jeep(4, 4));
        gameEngineE.plants.add(new Tree(5, 5, 3));

        gameEngineE.stop();
        assertTrue(gameEngineE.carnivores.isEmpty());
        assertTrue(gameEngineE.herbivores.isEmpty());
        assertTrue(gameEngineE.rangers.isEmpty());
        assertTrue(gameEngineE.tourists.isEmpty());
        assertTrue(gameEngineE.jeeps.isEmpty());
        assertTrue(gameEngineE.plants.isEmpty());

    }

    @Test
    void testBuyPlantDecreasesMoneyAndAddsToList() {
        Tree tree = new Tree(5, 5, 3);
        //plant.setPrice(500);

        gameEngineE.setMoney(10000);
        gameEngineE.buyPlant(tree);

        assertTrue(gameEngineE.plants.contains(tree));
        assertEquals(9200, gameEngineE.getMoney());
    }

    @Test
    void testBuyLakeDecreasesMoneyAndAddsToList() {
        Lake lake = new Lake(2, 2);
        //lake.setPrice(300);

        gameEngineE.setMoney(10000);
        gameEngineE.buyLake(lake);

        assertTrue(gameEngineE.lakes.contains(lake));
        assertEquals(7500, gameEngineE.getMoney());
    }

    @Test
    void testBuyRoadDecreasesMoneyAndAddsToList() {
        Road road = new Road(3, 3);
        //road.setPrice(200);

        gameEngineE.setMoney(10000);
        gameEngineE.buyRoad(road);

        assertTrue(gameEngineE.roads.contains(road));
        assertEquals(9950, gameEngineE.getMoney());
    }

    @Test
    void testBuyAnimalAddsAnimalDecreasesMoneySetsBornAtAndUpdatesTicketPrice() {
        Elephant elephant = new Elephant(4, 4, false);
        //elephant.setPrice(1000);

        gameEngineE.setMoney(10000);
        gameEngineE.setSpentTime(123.0);
        gameEngineE.buyAnimal(elephant);

        assertTrue(gameEngineE.herbivores.contains(elephant));
        assertEquals(7000, gameEngineE.getMoney());
        assertEquals(123.0, elephant.getBornAt());
        assertTrue(gameEngineE.getTicketPrice() > 100); // növekszik
        assertTrue(gameEngineE.getCanCheckForLose());
    }

    @Test
    void testSellAnimalRemovesFromListIncreasesMoneyAndUpdatesTicketPrice() {
        Elephant elephant = new Elephant(4, 4, false);
        //elephant.setPrice(1000);
        //elephant.setSellPrice(800);

        gameEngineE.setMoney(10000);
        gameEngineE.buyAnimal(elephant); // money: 7000

        gameEngineE.sellAnimalTest(elephant); //3000 * 3/5 = 1800 -at kapok érte

        assertFalse(gameEngineE.herbivores.contains(elephant));
        assertEquals(8800, gameEngineE.getMoney());
        assertTrue(gameEngineE.getTicketPrice() <= 100); // visszaesik az ár
    }

    @Test
    void testUpdateTicketPriceCorrectlyCalculatesTotal() {
        Elephant elephant = new Elephant(4, 4, false);
        Lion lion = new Lion(5, 5, false);

        gameEngineE.buyAnimal(elephant);
        gameEngineE.buyAnimal(lion);

        // ticketPrice = 100 + 300 (elephant/10) + 150 (lion/10)
        assertEquals(550, gameEngineE.getTicketPrice());
    }
}
