package simu.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import simu.framework.Clock; // Customer uses Clock
import simu.framework.Trace; // Import Trace
// import controller.Controller; // Import Controller - Not needed if passing null
import static org.junit.jupiter.api.Assertions.*;

class CustomerTest {

    @BeforeEach
    void setUp() {
        // Reset clock and customer ID counter before each test
        Clock.getInstance().setTime(0.0);
        Customer.resetIdCounter();
        // Initialize Trace level to prevent NullPointerException in Customer constructor
        Trace.setTraceLevel(Trace.Level.INFO); // Or Trace.Level.NONE if no output needed
    }

    @Test
    void testCustomerIdIncrement() {
        Customer c1 = new Customer(1L, null); // Pass 1L for EU, null for Controller
        assertEquals(1, c1.getId(), "First customer ID should be 1.");

        Customer c2 = new Customer(0L, null); // Pass 0L for non-EU, null for Controller
        assertEquals(2, c2.getId(), "Second customer ID should be 2.");
    }

    @Test
    void testArrivalAndRemovalTime() {
        // Set time before creating customer so arrival time is predictable
        Clock.getInstance().setTime(10.0);
        Customer customer = new Customer(1L, null); // Pass 1L for EU, null for Controller

        // Arrival time is set in constructor, let's check it
        assertEquals(10.0, customer.getArrivalTime(), "Arrival time should be set correctly in constructor.");

        Clock.getInstance().setTime(55.5);
        customer.setRemovalTime(Clock.getInstance().getTime());
        assertEquals(55.5, customer.getRemovalTime(), "Removal time should be set correctly.");
    }

    @Test
    void testGetTotalTimeInSystem() {
        // Set time before creating customer
        Clock.getInstance().setTime(5.0);
        Customer customer = new Customer(0L, null); // Pass 0L for non-EU, null for Controller

        // Arrival time is set to 5.0 by constructor
        assertEquals(5.0, customer.getArrivalTime(), "Arrival time should be 5.0");

        // Set removal time
        customer.setRemovalTime(25.0);
        assertEquals(20.0, customer.getTotalTimeInSystem(), "Total time in system should be removal - arrival.");
    }

     @Test
    void testGetIsEUFlight() {
        // Set time before creating customers
        Clock.getInstance().setTime(0.0);
        Customer euCustomer = new Customer(1L, null); // Pass 1L for EU, null for Controller
        assertTrue(euCustomer.getIsEUFlight(), "Customer should be marked as EU flight.");

        Customer nonEuCustomer = new Customer(0L, null); // Pass 0L for non-EU, null for Controller
        assertFalse(nonEuCustomer.getIsEUFlight(), "Customer should be marked as non-EU flight.");
    }
}
