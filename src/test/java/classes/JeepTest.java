package classes;

import classes.Jeep;
import classes.entities.Direction;
import classes.terrains.Ground;
import classes.terrains.Terrain;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class JeepTest {

    private Jeep jeep;

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
        jeep = new Jeep(100, 100);
    }

    @Test
    void testMoveUpdatesPositionAndDirection() {
        double originalX = jeep.getX();
        double originalY = jeep.getY();

        jeep.move(Direction.RIGHT, 10, 0); // jobbra 10-et

        assertEquals(originalX + 10, jeep.getX(), 0.1);
        assertEquals(originalY, jeep.getY(), 0.1);
        assertEquals(Direction.RIGHT, jeep.getCurrentDirection());
    }

    @Test
    void testMoveAlongPathMovesTowardTarget() {
        Ground ground = new Ground(10, 4);
        ArrayList<Terrain> path = new ArrayList<>();
        path.add(ground);

        jeep.setPath(path);
        double beforeX = jeep.getX();
        double beforeY = jeep.getY();

        jeep.moveAlongPath();

        double afterX = jeep.getX();
        double afterY = jeep.getY();

        assertTrue(afterX != beforeX || afterY != beforeY);
        assertEquals(0, jeep.getSpeciesSeen().size());
    }

    @Test
    void testMoveAlongPathEndsCorrectly() {
        Ground ground = new Ground((int) jeep.getY(), (int) jeep.getY());
        ArrayList<Terrain> path = new ArrayList<>();
        path.add(ground);

        jeep.setPath(path);
        jeep.moveAlongPath();

        assertTrue(jeep.getSpeciesSeen().isEmpty());
    }
}
