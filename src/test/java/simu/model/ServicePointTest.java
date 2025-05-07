package simu.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import eduni.distributions.ContinuousGenerator;
import simu.framework.Clock;
import simu.framework.Event;
import simu.framework.EventList;
import simu.framework.Trace;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Initialize Mockito
class ServicePointTest {

    @Mock
    private ContinuousGenerator generatorMock; // Mock the random number generator

    @Mock
    private EventList eventListMock; // Mock the event list

    @Mock
    private Customer customerMock1; // Mock customers to isolate ServicePoint logic

    @Mock
    private Customer customerMock2;

    private ServicePoint servicePoint;
    private final EventType testEventType = EventType.DEP1; // Example event type

    @BeforeEach
    void setUp() {
        // Reset clock and trace level for each test
        Clock.getInstance().setTime(0.0);
        Trace.setTraceLevel(Trace.Level.INFO); // Or NONE
        Customer.resetIdCounter(); // Reset customer IDs if needed

        // Create the ServicePoint instance with mocks
        servicePoint = new ServicePoint(generatorMock, eventListMock, testEventType);
    }

    @Test
    void testAddQueueIncreasesLength() {
        assertEquals(0, servicePoint.getQueueLength(), "Queue should be initially empty.");
        servicePoint.addQueue(customerMock1);
        assertEquals(1, servicePoint.getQueueLength(), "Queue length should be 1 after adding a customer.");
        servicePoint.addQueue(customerMock2);
        assertEquals(2, servicePoint.getQueueLength(), "Queue length should be 2 after adding another customer.");
    }

    @Test
    void testAddQueueCallsStartWaiting() {
        Clock.getInstance().setTime(5.0);
        servicePoint.addQueue(customerMock1);
        // Verify startWaiting was called without arguments
        verify(customerMock1).startWaiting();
    }


    @Test
    void testRemoveQueueDecreasesLengthAndReturnsCorrectCustomer() {
        servicePoint.addQueue(customerMock1);
        servicePoint.addQueue(customerMock2);
        assertEquals(2, servicePoint.getQueueLength(), "Queue length should be 2 before removal.");

        Customer removedCustomer = servicePoint.removeQueue();
        assertSame(customerMock1, removedCustomer, "First customer added should be removed first (FIFO).");
        assertEquals(1, servicePoint.getQueueLength(), "Queue length should be 1 after removal.");

        removedCustomer = servicePoint.removeQueue();
        assertSame(customerMock2, removedCustomer, "Second customer added should be removed second.");
        assertEquals(0, servicePoint.getQueueLength(), "Queue length should be 0 after removing all.");
    }

    @Test
    void testRemoveQueueCallsStopWaiting() {
        Clock.getInstance().setTime(10.0); // Time when customer is removed
        servicePoint.addQueue(customerMock1);
        Customer removed = servicePoint.removeQueue();

        assertNotNull(removed);
        // Verify stopWaiting was called without arguments
        verify(removed).stopWaiting();
    }

    @Test
    void testRemoveQueueFromEmptyReturnsNull() {
        assertNull(servicePoint.removeQueue(), "Removing from an empty queue should return null.");
        assertEquals(0, servicePoint.getQueueLength(), "Queue length should remain 0.");
    }

    @Test
    void testBeginServiceSetsReservedAndSchedulesEvent() {
        double expectedServiceTime = 15.0;
        double currentTime = 20.0;
        Clock.getInstance().setTime(currentTime);

        // Define mock behavior
        when(generatorMock.sample()).thenReturn(expectedServiceTime);

        assertFalse(servicePoint.isReserved(), "Service point should not be reserved initially.");

        servicePoint.beginService();

        assertTrue(servicePoint.isReserved(), "Service point should be reserved after starting service.");
        assertEquals(expectedServiceTime, servicePoint.getTotalServiceTime(), "Total service time should be updated.");

        // Capture the event added to the event list
        ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
        verify(eventListMock).add(eventCaptor.capture());

        Event scheduledEvent = eventCaptor.getValue();
        assertEquals(testEventType, scheduledEvent.getType(), "Scheduled event should have the correct type.");
        assertEquals(currentTime + expectedServiceTime, scheduledEvent.getTime(), "Scheduled event time should be current time + service time.");
        assertSame(servicePoint, scheduledEvent.getServicePoint(), "Scheduled event should reference this service point."); // Assuming Event has getServicePoint()

        // Verify generator was called
        verify(generatorMock).sample();
    }

    @Test
    void testIsOnQueue() {
        assertFalse(servicePoint.isOnQueue(), "isOnQueue should be false when empty.");
        servicePoint.addQueue(customerMock1);
        assertTrue(servicePoint.isOnQueue(), "isOnQueue should be true when not empty.");
        servicePoint.removeQueue();
        assertFalse(servicePoint.isOnQueue(), "isOnQueue should be false after removing the only customer.");
    }

     @Test
    void testCompareTo() {
        ServicePoint sp1 = new ServicePoint(generatorMock, eventListMock, testEventType);
        ServicePoint sp2 = new ServicePoint(generatorMock, eventListMock, testEventType);

        // sp1 has 1 customer, sp2 has 0
        sp1.addQueue(customerMock1);
        assertTrue(sp1.compareTo(sp2) > 0, "sp1 queue (1) > sp2 queue (0)");
        assertTrue(sp2.compareTo(sp1) < 0, "sp2 queue (0) < sp1 queue (1)");

        // sp1 has 1 customer, sp2 has 1
        sp2.addQueue(customerMock2);
        assertEquals(0, sp1.compareTo(sp2), "sp1 queue (1) == sp2 queue (1)");

        // sp1 has 1 customer, sp2 has 2
        sp2.addQueue(customerMock1); // Add another mock customer
         assertTrue(sp1.compareTo(sp2) < 0, "sp1 queue (1) < sp2 queue (2)");
         assertTrue(sp2.compareTo(sp1) > 0, "sp2 queue (2) > sp1 queue (1)");
    }

    @Test
    void testClear() {
        servicePoint.addQueue(customerMock1);
        servicePoint.beginService(); // Make it reserved

        assertTrue(servicePoint.isOnQueue());
        assertTrue(servicePoint.isReserved());

        servicePoint.clear();

        assertFalse(servicePoint.isOnQueue(), "Queue should be empty after clear.");
        assertEquals(0, servicePoint.getQueueLength(), "Queue length should be 0 after clear.");
        assertFalse(servicePoint.isReserved(), "Service point should not be reserved after clear.");
        // Note: totalServiceTime is likely NOT reset by clear, which might be intended.
    }
}