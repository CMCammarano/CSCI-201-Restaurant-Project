/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.restaurant.agent;

import core.agent.Agent;
import core.agent.Message;
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
		customer.state = CustomerStateEnum.Ordered;
		customer.customer.sendMessage("takeOrder");
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
		
		print("Seating customer " + customer.getName() + " at " + table.toString());
		stateChanged();
	}
	
	// Messages -- Customer
	public void madeChoice(Message message) {
		Customer customer = message.get(0);
		
		synchronized (m_customers) {
			for (CustomerHandler c : m_customers) {
				if (c.customer == customer) {
					c.state = CustomerStateEnum.ReadyToOrder;
				}
			}
		}
		print(customer.getName() + " wants to place an order.");
		stateChanged();
	}
	
	public void placeOrder(Message message) {
		Customer customer = message.get(0);
		String choice = message.get(1);
		
		print(customer.getName() + " chose to order " + choice);
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
		Asked,
		Ordered,
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
