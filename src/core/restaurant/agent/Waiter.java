/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.restaurant.agent;

import core.agent.Agent;
import core.agent.Message;
import core.restaurant.Check;
import core.restaurant.Order;
import core.restaurant.Table;
import gui.agents.WaiterGUI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Semaphore;

/**
 *
 * @author ColinCammarano
 */
public class Waiter extends Agent {

	private Host m_host;
	private Cashier m_cashier;
	private Cook m_cook;
	private HashMap<String, Float> m_menu;
	private final List<CustomerHandler> m_customers;
	
	// Animation
	private WaiterGUI m_gui;
	private final Semaphore m_atDestination;
	
	public Waiter(String name) {
		super(name);
		m_customers = Collections.synchronizedList(new ArrayList<CustomerHandler>());
		
		m_atDestination = new Semaphore(0, true);
	}
	
	private void takeCustomerToTable(CustomerHandler customer) {
		customer.state = CustomerStateEnum.Seated;
		
		doGoToCustomer();
		try {
			m_atDestination.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace(System.err);
		}
		
		customer.customer.sendMessage("sitAtTable", new Message(this, customer.table, m_menu));
		
		doTakeCustomerToTable(customer);
		try {
			m_atDestination.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace(System.err);
		}
		
		doGoToWaitingArea();
		try {
			m_atDestination.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace(System.err);
		}
	}
	
	private void takeCustomerOrder(CustomerHandler customer) {
		customer.state = CustomerStateEnum.Ordering;
		
		doTakeCustomerOrder(customer);
		try {
			m_atDestination.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace(System.err);
		}
		
		customer.customer.sendMessage("takeOrder");
	}
	
	private void sendOrderToCook(CustomerHandler customer) {
		print("Taking " + customer.customer.getName() + "'s order of " + customer.choice + " to the cook.");
		customer.state = CustomerStateEnum.WaitingForOrder;
		
		doSendOrderToCook(customer);
		try {
			m_atDestination.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace(System.err);
		}
		
		m_cook.sendMessage("takeOrder", new Message(new Order(this, customer.customer, customer.choice)));
	}
	
	private void bringFoodToCustomer(CustomerHandler customer) {
		print("Bringing " + customer.choice + " to " + customer.customer.getName() + ".");
		customer.state = CustomerStateEnum.Eating;
		
		doBringFoodToCustomer(customer);
		try {
			m_atDestination.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace(System.err);
		}
		
		customer.customer.sendMessage("receiveFood");
		
		doGoToWaitingArea();
		try {
			m_atDestination.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace(System.err);
		}
	}
	
	private void getCheckFromCashier(CustomerHandler customer) {
		print("Getting check for " + customer.customer.getName() + " from the cashier.");
		customer.state = CustomerStateEnum.WaitingForCashier;
		
		doPromptCheck(customer);
		try {
			m_atDestination.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace(System.err);
		}
		
		doGetCheckFromCashier();
		try {
			m_atDestination.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace(System.err);
		}
		
		m_cashier.sendMessage("computeCheck", new Message(this, customer.customer, customer.choice));
	}
	
	private void bringCheckToCustomer(CustomerHandler customer) {
		print("Bringing " + customer.customer.getName() + " his or her check.");
		customer.state = CustomerStateEnum.Paid;
		
		doBringCheckToCustomer(customer);
		try {
			m_atDestination.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace(System.err);
		}
		
		customer.customer.sendMessage("receiveCheck");
		
		doGoToWaitingArea();
		try {
			m_atDestination.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace(System.err);
		}
	}
	
	// Animation
	private void doGoToCustomer() {
		m_gui.doGoToCustomer();
	}
	
	private void doTakeCustomerToTable(CustomerHandler customer) {
		m_gui.doTakeCustomerToTable(customer.table);
	}
	
	private void doGoToWaitingArea() {
		m_gui.doGoToWaitingArea();
	}
	
	private void doTakeCustomerOrder(CustomerHandler customer) {
		m_gui.doTakeCustomerOrder(customer.table);
	}
	
	private void doSendOrderToCook(CustomerHandler customer) {
		m_gui.doSendOrderToCook(customer.choice);
	}
	
	private void doBringFoodToCustomer(CustomerHandler customer) {
		m_gui.doBringFoodToCustomer(customer.choice, customer.table);
	}
	
	private void doPromptCheck(CustomerHandler customer) {
		m_gui.doPromptCheck(customer.table);
	}
	
	private void doGetCheckFromCashier() {
		m_gui.doGetCheckFromCashier();
	}
	
	private void doBringCheckToCustomer(CustomerHandler customer) {
		m_gui.doBringCheckToCustomer(customer.table);
	}
	
	public void atDestination() {
		m_atDestination.release();
	}

	@Override
	public boolean update() {
		synchronized (m_customers) {
			for (CustomerHandler c : m_customers) {
				if (c.state == CustomerStateEnum.Waiting) {
					takeCustomerToTable(c);
					return true;
				}
			}
		}
		
		synchronized (m_customers) {
			for (CustomerHandler c : m_customers) {
				if (c.state == CustomerStateEnum.ReadyToOrder) {
					takeCustomerOrder(c);
					return true;
				}
			}
		}
				
		synchronized (m_customers) {
			for (CustomerHandler c : m_customers) {
				if (c.state == CustomerStateEnum.WaitingForFood) {
					bringFoodToCustomer(c);
					return true;
				}
			}
		}
				
		synchronized (m_customers) {
			for (CustomerHandler c : m_customers) {
				if (c.state == CustomerStateEnum.Ordered) {
					sendOrderToCook(c);
					return true;
				}
			}
		}
				
		synchronized (m_customers) {
			for (CustomerHandler c : m_customers) {
				if (c.state == CustomerStateEnum.ReadyToReceiveCheck) {
					getCheckFromCashier(c);
					return true;
				}
			}
		}
				
		synchronized (m_customers) {
			for (CustomerHandler c : m_customers) {
				if (c.state == CustomerStateEnum.ReadyToPay) {
					bringCheckToCustomer(c);
					return true;
				}
			}
		}
				
		synchronized (m_customers) {
			for (CustomerHandler c : m_customers) {
				if (c.state == CustomerStateEnum.Paid) {
					m_customers.remove(c);
					return true;
				}
			}
		}
		
		return false;
	}
	
	// Messages -- Host
	public void seatCustomer(Message message) {
		Customer customer = message.get(0);
		Table table = message.get(1);
		
		boolean found = false;
		for (CustomerHandler c : m_customers) {
			if (c.customer == customer) {
				found = true;
				break;
			}
		}

		if (!found) {
			CustomerHandler c = new CustomerHandler(customer, table);
			m_customers.add(c);
		}
		
		print("Seating customer " + customer.getName() + " at " + table.toString() + ".");
		stateChanged();
	}
	
	// Messages -- Customer
	public void madeChoice(Message message) {
		Customer customer = message.get(0);
		for (CustomerHandler c : m_customers) {
			if (c.customer == customer) {
				c.state = CustomerStateEnum.ReadyToOrder;
				break;
			}
		}
		
		print(customer.getName() + " wants to place an order.");
		stateChanged();
	}
	
	public void placeOrder(Message message) {
		Customer customer = message.get(0);
		String choice = message.get(1);
		for (CustomerHandler c : m_customers) {
			if (c.customer == customer) {
				c.state = CustomerStateEnum.Ordered;
				c.choice = choice;
				break;
			}
		}
		
		print(customer.getName() + " chose to order " + choice + ".");
		stateChanged();
	}
	
	public void askForCheck(Message message) {
		Customer customer = message.get(0);
		for (CustomerHandler c : m_customers) {
			if (c.customer == customer) {
				c.state = CustomerStateEnum.ReadyToReceiveCheck;
				break;
			}
		}
		
		print(customer.getName() + " asked us for his/her check.");
		stateChanged();
	}
	
	// Messages -- Cashier
	public void pickupCheck(Message message) {
		Check check = message.get(0);
		for (CustomerHandler c : m_customers) {
			if (c.customer == check.getCustomer()) {
				c.state = CustomerStateEnum.ReadyToPay;
			}
		}
		
		print("Picking " + check.getCustomer().getName() + "'s check up from the cashier.");
		stateChanged();
	}
	
	// Messages -- Cook
	public void pickupOrder(Message message) {
		Order order = message.get(0);
		for (CustomerHandler c : m_customers) {
			if (c.customer == order.getCustomer()) {
				c.state = CustomerStateEnum.WaitingForFood;
				break;
			}
		}
		
		print("Picked up " + order.getCustomer().getName() + "'s " + order.getChoice() + " from the cook.");
		stateChanged();
	}
	
	/* ACCESSORS AND MUTATORS */
	public WaiterGUI getWaiterGUI() { return m_gui; }
	public void setWaiterGUI(WaiterGUI gui) { m_gui = gui; }
	
	public Host getHost() { return m_host; }
	public void setHost(Host host) { m_host = host; }
	
	public Cashier getCashier() { return m_cashier; }
	public void setCashier(Cashier cashier) { m_cashier = cashier; }
	
	public Cook getCook() { return m_cook; }
	public void setCook(Cook cook) { m_cook = cook; }
	
	public void setMenu(HashMap<String, Float> menu) { m_menu = menu; }
	
	public int getNumberOfCustomers() { return m_customers.size(); }
	
	private class CustomerHandler {
		public Customer customer;
		public Table table;
		public CustomerStateEnum state;
		public String choice;
		
		public CustomerHandler(Customer customer, Table table) {
			this.customer = customer;
			this.table = table;
			state = CustomerStateEnum.Waiting;
		}
	}
	
	private enum CustomerStateEnum {
		Waiting,
		Seated,
		ReadyToOrder,
		Ordering,
		Ordered,
		WaitingForOrder,
		WaitingForFood,
		HasFood,
		Eating,
		ReadyToPay,
		WaitingForCashier,
		ReadyToReceiveCheck,
		HasCheck,
		Paid,
		Leaving,
		PaidAndLeaving,
		LeftEarly,
		Gone
	}
}
