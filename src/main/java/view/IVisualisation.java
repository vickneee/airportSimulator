package view;

import simu.model.Customer;

import java.util.List;

public interface IVisualisation {
	public void clearDisplay();
	public void newCustomer();
    public void update(List<Customer> customers);
    public void addCustomer(Customer customer);
}

