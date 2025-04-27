package view;

import java.util.List;

public interface IVisualisation {
    void clearDisplay();
    void newCustomer(); // Called by Controller, doesn't have Customer object
    void updateQueueLengths(List<List<Integer>> lengths);
}

