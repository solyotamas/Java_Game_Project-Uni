package classes.entities.human;

import classes.entities.Direction;
import classes.entities.animals.Carnivore;
import classes.entities.animals.AnimalState;
import classes.entities.animals.carnivores.Lion;
import classes.entities.animals.herbivores.Elephant;
import javafx.application.Platform;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static classes.entities.Direction.DOWN;
//import static classes.entities.Direction.UP;
import static org.junit.jupiter.api.Assertions.*;

class RangerTest {

    private Ranger ranger;

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
        ranger = new Ranger(100, 100);
    }

    @Test
    void testInitialState() {
        assertEquals(HumanState.IDLE, ranger.getState());
        assertEquals(Direction.RIGHT, ranger.currentDirection); // public fieldként elérhető, különben getter kéne
        assertNotNull(ranger.getImageView());
    }

    @Test
    void testPickNewTargetWithinBounds() {
        ranger.pickNewTarget();
        double x = ranger.targetX;
        double y = ranger.targetY;

        double minX = 5 * 30 + ranger.getImageView().getFitWidth() / 2;
        double maxX = 59 * 30 - ranger.getImageView().getFitWidth() / 2;
        double minY = ranger.getImageView().getFitHeight();
        double maxY = 31 * 30;

        assertTrue(x >= minX && x <= maxX);
        assertTrue(y >= minY && y <= maxY);
    }

    @Test
    void testRestingChangesStateAfterTime() {
        ranger.transitionTo(HumanState.RESTING);
        for (int i = 0; i < 200; i++) {
            ranger.rest();
        }
        assertEquals(HumanState.IDLE, ranger.getState());
    }

    @Test
    void testPauseAndResume() {
        ranger.transitionTo(HumanState.MOVING);
        ranger.transitionTo(HumanState.PAUSED);
        ranger.resume();
        assertEquals(HumanState.MOVING, ranger.getState());
    }

    @Test
    void testSetAndCheckPaymentDue() {
        ranger.setLastPaidHour(1000);
        assertTrue(ranger.isDueForPayment(1721)); // 721 > 720
        assertFalse(ranger.isDueForPayment(1700));
    }

    @Test
    void testChooseAndHuntPrey_AndMove() {
        Lion lion = new Lion(100.0, 200.0);

        ranger.choosePrey(lion);
        assertEquals(lion, ranger.getPrey());

        ranger.huntTarget(); // messze van, nem tudja elkapni
        assertNotEquals(HumanState.CAPTURED, ranger.getState());

        // közelre megyünk
        double dy = lion.getY() - ranger.getY();
        //lion.move(UP, 0, dy);
        // lion.move(ground, UP, 0, -100);
        ranger.move(DOWN, 0, dy);

        ranger.huntTarget(); // most elkapja
        System.out.println(ranger.getState());
        assertEquals(HumanState.CAPTURED, ranger.getState());
        assertEquals(AnimalState.PAUSED, lion.getState());
    }
}
