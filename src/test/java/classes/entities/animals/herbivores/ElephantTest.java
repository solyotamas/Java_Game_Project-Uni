package classes.entities.animals.herbivores;

import classes.entities.animals.AnimalState;
import javafx.application.Platform;
import javafx.scene.image.ImageView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;

//import javafx.embed.swing.JFXPanel;
import static org.junit.jupiter.api.Assertions.*;

class ElephantTest {

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
        assertEquals(500, elephant.getPrice());
        assertEquals(60, elephant.getLifeExpectancy());
        assertEquals(0, elephant.getAge());
    }

    @Test
    void testInitialUIState() {
        ImageView view = elephant.getImageView();
        assertNotNull(view);
        assertEquals(elephant.getDepth(), elephant.getY() + (88 * 0.6 / 2.0), 0.001);
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
}
