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
 * @author cmcammarano
 */
public class Host extends Agent {

	private final List<WaiterHandler> m_waiters;
	private final List<CustomerHandler> m_customers;
	
	public Host(String name) {
		super(name);
		
		m_waiters = Collections.synchronizedList(new ArrayList<WaiterHandler>());
		m_customers = Collections.synchronizedList(new ArrayList<CustomerHandler>());
	}
	
	private void customerEnteredRestaurant(Message message) {
		Customer customer = message.get(0);
		print("Customer " + customer.getName() + " entered the restaurant.");
		
		CustomerHandler c = new CustomerHandler();
		c.customer = customer;
		synchronized (m_customers) {
			boolean found = false;
			for (CustomerHandler cust : m_customers) {
				if (cust.customer == customer) {
					found = true;
					break;
				}
			}
			
			if (!found) {
				m_customers.add(c);
			}
		}
	}
	
	private void customerLeftRestaurant(Message message) {
		Customer customer = message.get(0);
		print("Customer " + customer.getName() + " left the restaurant.");
		
		synchronized (m_customers) {
			for (CustomerHandler cust : m_customers) {
				if (cust.customer == customer) {
					m_customers.remove(cust);
					break;
				}
			}
		}
	}
	
	@Override
	public boolean update() {
		return false;
	}
	
	public void addWaiter(Waiter waiter) {
		WaiterHandler w = new WaiterHandler();
		w.waiter = waiter;
		synchronized (m_waiters) {
			m_waiters.add(w);
		}
	}
	
	private class CustomerHandler {
		public Customer customer;
		public Table table;
	}
	
	private class WaiterHandler {
		public Waiter waiter;
		public Table table;
	}
}
