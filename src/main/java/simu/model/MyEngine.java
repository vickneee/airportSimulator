package simu.model;

import controller.IControllerMtoV;
import eduni.distributions.Negexp;
import eduni.distributions.Normal;
import simu.framework.Clock;
import simu.framework.Engine;
import simu.framework.ArrivalProcess;
import simu.framework.Event;


public class MyEngine extends Engine {
    private ArrivalProcess arrivalProcess;

    public MyEngine(IControllerMtoV controller, int arrivalInterval) { // NEW
        super(controller); // NEW

        servicePoints = new ServicePoint[5];

        servicePoints[0] = new ServicePoint(new Normal(10, 10), eventList, EventType.DEP1);//check-in
        servicePoints[1] = new ServicePoint(new Normal(7, 10), eventList, EventType.DEP2);//security check
        servicePoints[2] = new ServicePoint(new Normal(10, 3), eventList, EventType.DEP3);///gate for EU flight
        servicePoints[3] = new ServicePoint(new Normal(5, 10), eventList, EventType.DEP4);//passport control
        servicePoints[4] = new ServicePoint(new Normal(10, 3), eventList, EventType.DEP5);// gate for outside EU flight


        arrivalProcess = new ArrivalProcess(new Negexp(arrivalInterval, 5), eventList, EventType.ARR1);
    }

    @Override
    protected void initialization() {
        arrivalProcess.generateNext();     // First arrival in the system
    }

    @Override
    protected void runEvent(Event t) {  // B phase events
        Customer a;

        switch ((EventType) t.getType()) {
            case ARR1:
                servicePoints[0].addQueue(new Customer(euFlightGenerator.sample())); // Generates a value of either 1 or 0 using the Bernoulli distribution and passes it as a parameter to create a new Customer object,
                arrivalProcess.generateNext();
                controller.visualiseCustomer(); // NEW
                break;

            case DEP1://check-in
                a = servicePoints[0].removeQueue();
                servicePoints[1].addQueue(a);
                break;

            case DEP2://security check
                a = servicePoints[1].removeQueue();
                if(a.getIsEUFlight()){
                    servicePoints[2].addQueue(a);
                } else{
                    servicePoints[3].addQueue(a);
                }
                break;

            case DEP3://gate for EU flight
                a = servicePoints[2].removeQueue();
                a.setRemovalTime(Clock.getInstance().getTime());
                a.reportResults();
                break;

            case DEP4:// passport control
                a = servicePoints[3].removeQueue();
                servicePoints[4].addQueue(a);
                break;

            case DEP5:// gate for outside EU flight
                a = servicePoints[4].removeQueue();
                a.setRemovalTime(Clock.getInstance().getTime());
                a.reportResults();
                break;
        }
    }

    @Override
    protected void results() {
        // OLD text UI
        //System.out.println("Simulation ended at " + Clock.getInstance().getClock());
        //System.out.println("Results ... are currently missing");

        // NEW GUI
        controller.showEndTime(Clock.getInstance().getTime());
    }
}
