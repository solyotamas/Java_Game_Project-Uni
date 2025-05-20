package classes.entities.animals.herbivores;

import classes.entities.animals.AnimalState;
import classes.terrains.Ground;
import classes.terrains.Terrain;
import javafx.application.Platform;
import javafx.scene.image.ImageView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;

//import javafx.embed.swing.JFXPanel;
import static classes.entities.Direction.*;
import static org.junit.jupiter.api.Assertions.*;

//@DisabledIfEnvironmentVariable(named = "CI", matches = "true")
public class ElephantTest {

    private Elephant elephant;

    @BeforeAll
    public static void initJavaFX() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException e) {
            // JavaFX már elindult
        }
    }

    @BeforeEach
    void setUp() {
        elephant = new Elephant(100.0, 200.0);
    }

    @Test
    void testConstructorAndGetters() {
        assertEquals(100.0, elephant.getX(), 0.001);
        assertTrue(elephant.getY() > 200.0); // offsettel számolva
        assertEquals(90, elephant.getFrameWidth());
        assertEquals(88, elephant.getFrameHeight());
        assertEquals(0.4, elephant.getSpeed(), 0.001);
        assertEquals(3000, elephant.getPrice());
        assertEquals(60, elephant.getLifeExpectancy());
        assertEquals(0, elephant.getAge());
    }

    @Test
    void testInitialAnimalStats() {
        assertEquals(AnimalState.IDLE, elephant.getState());
        assertEquals(100.0, elephant.getThirst());
        assertEquals(100.0, elephant.getHunger());
        assertTrue(elephant.getPath().isEmpty());
        assertNull(elephant.getStart());
        assertNull(elephant.getTarget());
    }

//    @Test
//    void testInitialUIState() {
//        ImageView view = elephant.getImageView();
//        assertNotNull(view);
//        assertEquals(elephant.getDepth(), elephant.getY() + (88 * 0.6 / 2.0), 0.001);
//    }
//    @Test
//    @Tag("gui")
//    @DisabledIfEnvironmentVariable(named = "CI", matches = "true")
//    void testInitialUIState() {
//        ImageView view = elephant.getImageView();
//        assertNotNull(view);
//        assertEquals(elephant.getDepth(), elephant.getY() + (88 * 0.6 / 2.0), 0.001);
//    }

    @Test
    void testDrinks() {
        elephant.changeThirst(-50);
        elephant.drink();
        assertTrue(elephant.getThirst() > 50); //mert alapból ugye 20 a kontrusktorban
    }

    @Test
    void testEats() {
        elephant.changeHunger(-50);
        elephant.eat();
        assertTrue(elephant.getHunger() >= 50); //ez is 20
    }

    @Test
    void testRestAndTransitionTo() {
        elephant.transitionTo(AnimalState.RESTING);
        for (int i = 0; i < 400; i++) {
            elephant.rest();
        }
        assertEquals(AnimalState.IDLE, elephant.getState());

        //egyik sem nyúl az éhséghez vagy szomjúsághoz
        assertEquals(100.0, elephant.getThirst());
        assertEquals(100.0, elephant.getHunger());
    }

    @Test
    /*void testMove() {
        double initialX = elephant.getLayoutX();
        elephant.move(RIGHT, 1, 0);
        assertEquals(initialX + 1, elephant.getLayoutX());
        assertEquals(RIGHT, elephant.getCurrentDirection());

        initialX = elephant.getLayoutX();
        elephant.move(LEFT, -1, 0);
        assertEquals(initialX - 1, elephant.getLayoutX());
        assertEquals(LEFT, elephant.getCurrentDirection());

        double initialY = elephant.getLayoutY();
        elephant.move(UP, 0, -1);
        assertEquals(initialY - 1 , elephant.getLayoutY());
        assertEquals(UP, elephant.getCurrentDirection());

        initialY = elephant.getLayoutY();
        elephant.move(DOWN, 0, 1);
        assertEquals(initialY + 1, elephant.getLayoutY());
        assertEquals(DOWN, elephant.getCurrentDirection());
    }*/


    void testMove() {
        Ground ground = new Ground(10,4);
        double initialX = elephant.getLayoutX();

        elephant.move(ground, RIGHT, 1, 0);
        assertEquals(initialX + 1, elephant.getLayoutX());
        assertEquals(RIGHT, elephant.getCurrentDirection());

        initialX = elephant.getLayoutX();
        elephant.move(ground, LEFT, -1, 0);
        assertEquals(initialX - 1, elephant.getLayoutX());
        assertEquals(LEFT, elephant.getCurrentDirection());

        double initialY = elephant.getLayoutY();
        elephant.move(ground, UP, 0, -1);
        assertEquals(initialY - 1 , elephant.getLayoutY());
        assertEquals(UP, elephant.getCurrentDirection());

        initialY = elephant.getLayoutY();
        elephant.move(ground, DOWN, 0, 1);
        assertEquals(initialY + 1, elephant.getLayoutY());
        assertEquals(DOWN, elephant.getCurrentDirection());
    }


    @Test
    public void testMoveTowardsLeader() {
        Elephant leader = new Elephant(100, 100);
        Elephant follower = new Elephant(50, 50);
        leader.transitionTo(AnimalState.RESTING);

        Ground ground = new Ground(10,4);
        follower.moveTowardsLeader(leader, ground);

        assertTrue(follower.getX() != 50 || follower.getY() != 50); // Pozíció változott
        // Ha elég közel van, akkor az állapot öröklődik
        for (int i = 0; i < 200; i++) {
            follower.moveTowardsLeader(leader, ground);
        }

        follower.moveTowardsLeader(leader, ground);
        //System.out.println("leader: " + leader.getX() + ", " + leader.getY());
        //System.out.println("follower: " + follower.getX() + ", " + follower.getY());
        assertEquals(AnimalState.RESTING, follower.getState());
    }

    @Test
    public void testSetBornAtAndAgingAnimal() {
        //Elephant elephant = new Elephant(100, 100);
        elephant.setBornAt(0.0);
        elephant.agingAnimal(168.0 * 10); // 10 nap múlva

        assertEquals(elephant.getStartingAge() + 10, elephant.getAge());
        assertTrue(elephant.getAppetite() >= 1); // étvágy nő az idővel
    }

    @Test
    public void testOldEnoughToDie() {
        //Elephant elephant = new Elephant(100, 100);
        elephant.setBornAt(0.0);

        // idő még nem telt el (nem hal meg)
        assertFalse(elephant.oldEnoughToDie(24.0)); // 1 nap

        // épp elérte a várható élettartamot
        double hoursUntilDeath = (elephant.getLifeExpectancy() - elephant.getStartingAge()) * 168.0;
        assertTrue(elephant.oldEnoughToDie(hoursUntilDeath + 0.1));
    }

    @Test
    public void testGetSellPrice() {
        //Elephant elephant = new Elephant(100, 100);
        int base = elephant.getPrice() * 3 / 5;
        int ageSegment = elephant.getLifeExpectancy() / 5;

        for (int i = 0; i <= 5; i++) {
            elephant.setBornAt(0);
            elephant.agingAnimal((elephant.getStartingAge() + i * ageSegment) * 168.0);
            int price = elephant.getSellPrice();

            int expected = switch (i) {
                case 0 -> base * 5 / 5;
                case 1 -> base * 4 / 5;
                case 2 -> base * 3 / 5;
                case 3 -> base * 2 / 5;
                default -> base / 5;
            };
            assertEquals(expected, price);
        }
    }



}
