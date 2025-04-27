package classes;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class AlwaysTrueTest {

    @Test
    void testAlwaysTrue() {
        assertTrue(true, "Ez a teszt mindig sikeres.");
    }
}
