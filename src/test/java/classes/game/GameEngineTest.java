package classes.game;

import classes.Difficulty;
import classes.controllers.GameController;
import classes.entities.animals.carnivores.Lion;
import classes.entities.animals.herbivores.Elephant;
import classes.entities.human.Ranger;
//import classes.map.GameMap;
import classes.entities.human.Tourist;
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
            Platform.startup(() -> {});
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
        gameEngineE.getHerbivores().add(new Elephant(0, 0));
        gameEngineE.getCarnivores().add(new Lion(1, 1));

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
            gameEngineE.herbivores.add(new Elephant(0, 0));
            gameEngineE.carnivores.add(new Lion(1, 1));
            gameEngineE.tourists.add(new Tourist(2,2, 0));
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
            gameEngineE.herbivores.add(new Elephant(0, 0));
            gameEngineE.carnivores.add(new Lion(1, 1));
            gameEngineE.tourists.add(new Tourist(2, 2, 0));
        }

        //assertFalse(gameEngineE.gameWon());

        gameEngineE.setSpentTime(50);
        assertFalse(gameEngineE.gameWon());

        // rossz feltételek
        gameEngineE.herbivores.clear();
        assertFalse(gameEngineE.gameWon()); // canCheckForWin ugrik

        for (int i = 0; i < 15; i++) {
            gameEngineE.herbivores.add(new Elephant(0, 0));
        }
        //teszteli hogy a canCheckFroWin átáll e
        gameEngineE.setSpentTime(20);
        assertFalse(gameEngineE.gameWon());

        gameEngineE.setSpentTime(1000);
        assertTrue(gameEngineE.gameWon());
    }




}
