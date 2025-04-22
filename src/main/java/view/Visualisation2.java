//package view;
//
//import javafx.scene.canvas.Canvas;
//import javafx.scene.canvas.GraphicsContext;
//import javafx.scene.paint.Color;
//import javafx.scene.text.Font;
//import simu.model.Customer;
//
//public class Visualisation2 extends Canvas implements IVisualisation {
//	private GraphicsContext gc;
//	int customerCount = 0;
//
//	public Visualisation2(int w, int h) {
//		super(w, h);
//		gc = this.getGraphicsContext2D();
//		clearDisplay();
//	}
//
//	public void clearDisplay() {
//		gc.setFill(Color.YELLOW);
//		gc.fillRect(0, 0, this.getWidth(), this.getHeight());
//	}
//
//	public void newCustomer() {
//		customerCount++;
//
//		gc.setFill(Color.YELLOW);					// first erase old text
//		gc.fillRect(100,80, 130, 20);
//		gc.setFill(Color.RED);						// then write new text
//		gc.setFont(new Font(20));
//		gc.fillText("Customer " + customerCount, 100, 100);
//	}
//
//    @Override
//    public void addCustomer(Customer customer) {
//        // Implementation for adding a customer
//    }
//
//    @Override
//    public void update(List<Customer> customers) {
//        // Implementation for updating the display with a list of customers
//    }
//}
