package classes.entities.animals;

import classes.entities.animals.herbivores.Elephant;
import javafx.application.Platform;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class HerdTest {

    private Herd herd;
    private Elephant eli1, eli2, eli3;

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
        eli1 = new Elephant(100.0, 200.0, false);
        eli2 = new Elephant(150.0, 200.0, false);
        eli3 = new Elephant(200.0, 200.0, false);
        ArrayList<Animal> animals = new ArrayList<>();
        animals.add(eli1);
        animals.add(eli2);
        animals.add(eli3);
        herd = new Herd(animals);
    }

    @Test
    void testConstructorAndInitialization() {
        assertEquals(3, herd.getMemberCount());
        assertNotNull(herd.getLeader());
        for (Animal animal : herd.getMembers()) {
            assertTrue(animal.getIsInAHerd());
            assertEquals(herd, animal.getHerd());
        }
    }

    @Test
    void testAddMember() {
        Elephant eli4 = new Elephant(250.0, 200.0, false);
        herd.addMember(eli4);

        assertEquals(4, herd.getMemberCount());
        assertTrue(eli4.getIsInAHerd());
        assertEquals(herd, eli4.getHerd());
    }

    @Test
    void testMaxSizeLimit() {
        herd.addMember(new Elephant(300.0, 200.0, false)); // 5. tag
        herd.addMember(new Elephant(350.0, 200.0, false)); // 6. tag
        Elephant eli7 = new Elephant(400.0, 200.0, false);
        herd.addMember(eli7); // már nem fér be

        assertFalse(herd.getMembers().contains(eli7));
        assertEquals(5, herd.getMemberCount());
    }

    @Test
    void testRemoveLeaderAndLeaderReassignment() {
        Animal oldLeader = herd.getLeader();
        herd.removeMember(oldLeader);
        herd.assignNewLeader();

        if (herd.getMemberCount() > 0) {
            assertNotEquals(oldLeader, herd.getLeader());
            assertEquals(herd.getLeader(), herd.getMembers().get(0));
        } else {
            assertNull(herd.getLeader());
        }
    }

    @Test
    void testRemoveUntilDisbandsHerd() {
        herd.removeMember(eli1);
        herd.removeMember(eli2);
        herd.removeMember(eli3);

        assertEquals(0, herd.getMemberCount());
        assertNull(herd.getLeader());
    }

    @Test
    void testSetThirstAndHungerLevels() {
        eli1.setThirst(10);
        eli2.setThirst(20);
        eli3.setThirst(30);

        eli1.setHunger(15);
        eli2.setHunger(25);
        eli3.setHunger(35);

        herd.setThirstAndHungerLevels();

        assertEquals(20.0, eli1.getThirst());
        assertEquals(eli1.getThirst(), eli2.getThirst());
        assertEquals(eli3.getThirst(), eli2.getThirst());

        assertEquals(25.0, eli2.getHunger());
        assertEquals(eli1.getHunger(), eli2.getHunger());
        assertEquals(eli3.getHunger(), eli2.getHunger());
    }

}
