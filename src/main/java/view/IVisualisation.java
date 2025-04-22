package view;

import simu.model.Customer;

import java.util.List;

public interface IVisualisation {
    void clearDisplay();
    void newCustomer(); // Called by Controller, doesn't have Customer object
    /*void newCustomer(Customer customer); // Called by Controller with Customer object*/
    /*void moveCustomer(int customerId, String toLocation);*/
    void updateQueueLengths(List<List<Integer>> lengths);
    /*void addCustomer(Customer customer);*/
}

