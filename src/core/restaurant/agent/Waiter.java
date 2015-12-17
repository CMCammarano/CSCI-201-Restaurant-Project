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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author ColinCammarano
 */
public class Waiter extends Agent {

	private Host m_host;
	private Cashier m_cashier;
	private Cook m_cook;
	private List<CustomerHandler> m_customers;
	
	public Waiter(String name) {
		super(name);
		
		m_customers = Collections.synchronizedList(new ArrayList<CustomerHandler>());
	}
	
	private void takeCustomerToTable(CustomerHandler customer) {
		customer.state = CustomerStateEnum.Seated;
		customer.customer.sendMessage("sitAtTable", new Message(this, customer.table));
	}
	
	private void takeCustomerOrder(CustomerHandler customer) {
		customer.state = CustomerStateEnum.Ordering;
		customer.customer.sendMessage("takeOrder");
	}
	
	private void sendOrderToCook(CustomerHandler customer) {
		print("Taking this order of " + customer.choice + " to the cook.");
		customer.state = CustomerStateEnum.WaitingForOrder;
		m_cook.sendMessage("takeOrder", new Message(new Order(this, customer.customer, customer.choice)));
	}
	
	private void bringFoodToCustomer(CustomerHandler customer) {
		print("Bringing " + customer.choice + " to " + customer.customer.getName() + ".");
		customer.state = CustomerStateEnum.Eating;
		customer.customer.sendMessage("receiveFood");
	}
	
	private void getCheckFromCashier(CustomerHandler customer) {
		print("Getting check for " + customer.choice + " from the cashier.");
		customer.state = CustomerStateEnum.WaitingForCashier;
		m_cashier.sendMessage("computeCheck", new Message(this, customer.customer, customer.choice));
	}
	
	private void bringCheckToCustomer(CustomerHandler customer) {
		print("Bringing " + customer.customer.getName() + " his or her check.");
		customer.state = CustomerStateEnum.Paid;
		
		synchronized(m_customers) {
			m_customers.remove(customer);
		}
		
		customer.customer.sendMessage("receiveCheck");
	}

	@Override
	public boolean update() {
		synchronized (m_customers) {
			for (CustomerHandler c : m_customers) {
				if (c.state == CustomerStateEnum.Waiting) {
					takeCustomerToTable(c);
					return true;
				}
				
				if (c.state == CustomerStateEnum.ReadyToOrder) {
					takeCustomerOrder(c);
					return true;
				}
				
				if (c.state == CustomerStateEnum.Ordered) {
					sendOrderToCook(c);
					return true;
				}
				
				if (c.state == CustomerStateEnum.WaitingForFood) {
					bringFoodToCustomer(c);
					return true;
				}
				
				if (c.state == CustomerStateEnum.ReadyToReceiveCheck) {
					getCheckFromCashier(c);
					return true;
				}
				
				if (c.state == CustomerStateEnum.ReadyToPay) {
					bringCheckToCustomer(c);
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
		
		synchronized (m_customers) {
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
		}
		
		print("Seating customer " + customer.getName() + " at " + table.toString() + ".");
		stateChanged();
	}
	
	// Messages -- Customer
	public void madeChoice(Message message) {
		Customer customer = message.get(0);
		synchronized (m_customers) {
			for (CustomerHandler c : m_customers) {
				if (c.customer == customer) {
					c.state = CustomerStateEnum.ReadyToOrder;
					break;
				}
			}
		}
		print(customer.getName() + " wants to place an order.");
		stateChanged();
	}
	
	public void placeOrder(Message message) {
		Customer customer = message.get(0);
		String choice = message.get(1);
		synchronized (m_customers) {
			for (CustomerHandler c : m_customers) {
				if (c.customer == customer) {
					c.state = CustomerStateEnum.Ordered;
					c.choice = choice;
					break;
				}
			}
		}
		
		print(customer.getName() + " chose to order " + choice + ".");
		stateChanged();
	}
	
	public void askForCheck(Message message) {
		Customer customer = message.get(0);
		synchronized (m_customers) {
			for (CustomerHandler c : m_customers) {
				if (c.customer == customer) {
					c.state = CustomerStateEnum.ReadyToReceiveCheck;
					break;
				}
			}
		}
		
		print(customer.getName() + " asked us for his/her check.");
		stateChanged();
	}
	
	// Messages -- Cashier
	public void pickupCheck(Message message) {
		Check check = message.get(0);
		synchronized (m_customers) {
			for (CustomerHandler c : m_customers) {
				if (c.customer == check.getCustomer()) {
					c.state = CustomerStateEnum.ReadyToPay;
				}
			}
		}
		print("Picking " + check.getCustomer().getName() + "'s check up from the cashier.");
		stateChanged();
	}
	
	// Messages -- Cook
	public void pickupOrder(Message message) {
		Order order = message.get(0);
		print("Picked up order of " + order.getChoice() + " from the cook.");

		synchronized (m_customers) {
			for (CustomerHandler c : m_customers) {
				if (c.customer == order.getCustomer()) {
					c.state = CustomerStateEnum.WaitingForFood;
					break;
				}
			}
		}
		stateChanged();
	}
	
	/* ACCESSORS AND MUTATORS */
	public Host getHost() { return m_host; }
	public void setHost(Host host) { m_host = host; }
	
	public Cashier getCashier() { return m_cashier; }
	public void setCashier(Cashier cashier) { m_cashier = cashier; }
	
	public Cook getCook() { return m_cook; }
	public void setCook(Cook cook) { m_cook = cook; }
	
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
