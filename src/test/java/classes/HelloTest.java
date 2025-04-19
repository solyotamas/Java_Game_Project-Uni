package classes;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class HelloTest {
    @Test
    void testGreet() {
        Hello h = new Hello();
        assertEquals("Hello", h.greet());
    }
}
