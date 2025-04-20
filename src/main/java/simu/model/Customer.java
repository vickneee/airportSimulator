package simu.model;

    import simu.framework.Clock;
    import simu.framework.Trace;

    public class Customer {
        private double arrivalTime;
        private double removalTime;
        private int id;
        private static int i = 1;
        private static long sum = 0;
        private boolean isEUFlight;
        private String location;

        public Customer(long isEUFlight) {
            id = i++;
            if(isEUFlight == 1){
                this.isEUFlight = true;
            }else{
                this.isEUFlight = false;
            }
            arrivalTime = Clock.getInstance().getTime();
            Trace.out(Trace.Level.INFO, "New customer #" + id + " arrived at  " + arrivalTime);
        }

        public double getRemovalTime() {
            return removalTime;
        }

        public void setRemovalTime(double removalTime) {
            this.removalTime = removalTime;
        }

        public double getArrivalTime() {
            return arrivalTime;
        }

        public void setArrivalTime(double arrivalTime) {
            this.arrivalTime = arrivalTime;
        }

        public boolean getIsEUFlight(){
            return isEUFlight;
        }

        public int getId() {
            return id;
        }

        public void reportResults() {
            Trace.out(Trace.Level.INFO, "\nCustomer " + id + " ready! ");
            Trace.out(Trace.Level.INFO, "Customer "   + id + " arrived: " + arrivalTime);
            Trace.out(Trace.Level.INFO,"Customer "    + id + " removed: " + removalTime);
            Trace.out(Trace.Level.INFO,"Customer "    + id + " stayed: "  + (removalTime - arrivalTime));
            Trace.out(Trace.Level.INFO, "Customer " + id + " flight type: " + (isEUFlight? "EU flight" : "Non-EU flight"));

            sum += (removalTime - arrivalTime);
            double mean = sum/id;
            System.out.println("Current mean of the customer service times " + mean);
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

    }