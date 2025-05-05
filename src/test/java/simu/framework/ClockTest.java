package simu.framework;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ClockTest {

    private Clock clock;

    @BeforeEach
    void setUp() {
        // Reset the singleton instance before each test if necessary
        // (Requires modification of Clock class or reflection, often avoided)
        // For simplicity, we'll just get the instance.
        clock = Clock.getInstance();
        // Explicitly set time to 0 for predictability if tests run in parallel or sequence
        clock.setTime(0.0);
    }

    @Test
    void testGetInstance() {
        assertNotNull(Clock.getInstance(), "getInstance should return a non-null Clock instance.");
        assertSame(clock, Clock.getInstance(), "getInstance should return the same instance.");
    }

    @Test
    void testSetAndGetTime() {
        assertEquals(0.0, clock.getTime(), "Initial time should be 0.0 after setup.");
        clock.setTime(123.45);
        assertEquals(123.45, clock.getTime(), "Time should be set correctly.");
        clock.setTime(50.0);
        assertEquals(50.0, clock.getTime(), "Time should be updated correctly.");
    }
}
