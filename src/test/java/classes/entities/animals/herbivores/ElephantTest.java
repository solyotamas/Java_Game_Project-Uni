package classes.entities.animals.herbivores;

import classes.entities.animals.AnimalState;
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
//    public static void initJavaFX() {
//        new JFXPanel(); // Ezzel elindul a JavaFX környezet
//    }

//    @BeforeAll
//    static void initToolkit() throws InterruptedException {
//        final CountDownLatch latch = new CountDownLatch(1);
//        Platform.startup(() -> {
//            // semmi dolgunk, csak elindítjuk
//            latch.countDown();
//        });
//        latch.await();
//    }

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
        assertEquals(500, elephant.getPrice());
        assertEquals(60, elephant.getLifeExpectancy());
        assertEquals(0, elephant.getAge());
    }

    @Test
    void testInitialAnimalStats() {
        assertEquals(AnimalState.IDLE, elephant.getState());
        assertEquals(100.0, elephant.getThirst(), 0.001);
        assertEquals(100.0, elephant.getHunger(), 0.001);
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
    @Test
    @Tag("gui")
    @DisabledIfEnvironmentVariable(named = "CI", matches = "true")
    void testInitialUIState() {
        ImageView view = elephant.getImageView();
        assertNotNull(view);
        assertEquals(elephant.getDepth(), elephant.getY() + (88 * 0.6 / 2.0), 0.001);
    }

    @Test
    void testDrinks() {
        elephant.changeThirst(-50);
        elephant.drink();
        assertTrue(elephant.getThirst() > 50); //mert alapból ugye 100 a kontrusktorban
    }

    @Test
    void testEats() {
        elephant.changeHunger(-60);
        elephant.eat();
        assertTrue(elephant.getHunger() > 40); //ez is 100
    }

    @Test
    void testRestAndSTransitionTo() {
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
    void testMove() {
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
    }




}
