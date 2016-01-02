package gui.panels;

import core.restaurant.Restaurant;
import core.restaurant.agent.Customer;
import core.restaurant.agent.Host;
import core.restaurant.agent.Waiter;
import gui.RestaurantGUI;
import gui.agents.CashierGUI;
import gui.agents.CookGUI;
import gui.agents.CustomerGUI;
import gui.agents.HostGUI;
import gui.agents.WaiterGUI;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Panel in frame that contains all the restaurant information,
 * including host, cook, waiters, and customers.
 */
public class RestaurantPanel extends JPanel {
	
	// Static final variables
	private static final int ROWS = 1;
	private static final int COLLUMNS = 2;	
	private static final int EDGE_PADDING = 20;
	
	private static final int WAITER_X_POS = 200;
	private static final int WAITER_Y_POS = 500;

	//Host, cook, waiters and customers
	private final Vector<Customer> m_customers;
	private final Vector<Waiter> m_waiters;

	private final JPanel m_restaurantLabel;
	private final ListPanel m_customerPanel;
	private final JPanel m_group;

	private final Restaurant m_restaurant;
	private final RestaurantGUI m_gui;
	
	public RestaurantPanel(Restaurant restaurant, RestaurantGUI gui) {
		m_restaurant = restaurant;
		m_gui = gui;
		
		// Set up cook
		CookGUI cookGUI = new CookGUI(m_restaurant.getCook());
		m_restaurant.getCook().setCookGUI(cookGUI);
		m_gui.getAnimationPanel().addGui(cookGUI);
		
		// Set up cashier
		CashierGUI cashierGUI = new CashierGUI(m_restaurant.getCashier());
		m_restaurant.getCashier().setCashierGUI(cashierGUI);
		m_gui.getAnimationPanel().addGui(cashierGUI);

		// Set up host
		HostGUI hostGUI = new HostGUI(m_restaurant.getHost());
		m_restaurant.getHost().setHostGUI(hostGUI);
		m_gui.getAnimationPanel().addGui(hostGUI);
		
		m_customers = new Vector<Customer>();
		m_waiters = new Vector<Waiter>();
		
		m_restaurantLabel = new JPanel();
		m_customerPanel = new ListPanel(this, "Customers");
		m_group = new JPanel();
				
		setLayout(new GridLayout(ROWS, COLLUMNS, EDGE_PADDING, EDGE_PADDING));
		m_group.setLayout(new GridLayout(ROWS, COLLUMNS, EDGE_PADDING, EDGE_PADDING));
		m_group.add(m_customerPanel);

		initRestLabel();
		add(m_restaurantLabel);
		add(m_group);
	}

	/**
	 * Sets up the restaurant label that includes the menu,
	 * and host and cook information
	 */
	private void initRestLabel() {
		JLabel label = new JLabel();
		//restLabel.setLayout(new BoxLayout((Container)restLabel, BoxLayout.Y_AXIS));
		m_restaurantLabel.setLayout(new BorderLayout());
		label.setText("<html><h3><u>Your Lovely Staff</u></h3>" + "<table><tr><td>Host:</td><td>" + m_restaurant.getHost().getName() + "<tr><td>Cook:</td><td>" + m_restaurant.getCook().getName() + "</td></tr></table><h3><u> Menu</u></h3><table><tr><td>Steak</td><td>$15.99</td></tr><tr><td>Chicken</td><td>$10.99</td></tr><tr><td>Salad</td><td>$5.99</td></tr><tr><td>Pizza</td><td>$8.99</td></tr></table><br></html>");

		m_restaurantLabel.setBorder(BorderFactory.createRaisedBevelBorder());
		m_restaurantLabel.add(label, BorderLayout.CENTER);
		m_restaurantLabel.add(new JLabel("			   "), BorderLayout.EAST);
		m_restaurantLabel.add(new JLabel("			   "), BorderLayout.WEST);
	}

	/**
	 * When a customer or waiter is clicked, this function calls
	 * updatedInfoPanel() from the main gui so that person's information
	 * will be shown
	 *
	 * @param type indicates whether the person is a customer or waiter
	 * @param name name of person
	 */
	public void showInfo(String type, String name) {

		if (type.equals("Customers")) {

			for (int i = 0; i < m_customers.size(); i++) {
				Customer temp = m_customers.get(i);
				if (temp.getName().equals(name))
					m_gui.updateInfoPanel(temp);
			}
		}
		
		if (type.equals("Waiters")) {

			for (int i = 0; i < m_waiters.size(); i++) {
				Waiter temp = m_waiters.get(i);
				if (temp.getName().equals(name))
					m_gui.updateInfoPanel(temp);
			}
		}
	}

	/**
	 * Adds a customer or waiter to the appropriate list
	 *
	 * @param type indicates whether the person is a customer or waiter (later)
	 * @param name name of person
	 */
	public void addPerson(String type, String name) {

		if (type.equals("Customers")) {
			Customer c = new Customer(name);	
			CustomerGUI g = new CustomerGUI(c, m_gui);

			m_gui.getAnimationPanel().addGui(g);// dw
			c.setHost(m_restaurant.getHost());
			c.setCashier(m_restaurant.getCashier());
			c.setCustomerGUI(g);
			m_customers.add(c);
			c.startThread();
		}
		
		if (type.equals("Waiters")) {
			System.out.println("Adding new waiter: " + name);
			Waiter waiter = m_restaurant.addWaiter(name);	
			m_waiters.add(waiter);
			
			WaiterGUI gui = new WaiterGUI(waiter, WAITER_X_POS + ((m_waiters.size() - 1) * 25), WAITER_Y_POS);
			m_gui.getAnimationPanel().addGui(gui);
			waiter.setWaiterGUI(gui);
		}
	}

	public void pause() {
		System.out.println("Pausing agents.");
		m_restaurant.getHost().pause();
		m_restaurant.getCook().pause();
		m_restaurant.getCashier().pause();
		
		for (Customer c : m_customers) {
			c.pause();
			c.getCustomerGUI().pause();
		}
		
		for (Waiter w : m_waiters) {
			w.pause();
			w.getWaiterGUI().pause();
		}
	}
	
	public void resume() {
		System.out.println("Resuming agents.");
		m_restaurant.getHost().resume();
		m_restaurant.getCook().resume();
		m_restaurant.getCashier().resume();
		
		for (Customer c : m_customers) {
			c.resume();
			c.getCustomerGUI().resume();
		}
		
		for (Waiter w : m_waiters) {
			w.resume();
			w.getWaiterGUI().resume();
		}
	}
	
	// Returns the last customer in the list
	public Customer getLastCustomer() {
		return m_customers.lastElement();
	}
	
	public void addTable(int x, int y) {
		m_restaurant.addTable(x, y);
	}
	
	public Host getHost() {
		return m_restaurant.getHost();
	}
}