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
	private final List<Table> m_tables;
	
	public Host(String name) {
		super(name);
		
		m_waiters = Collections.synchronizedList(new ArrayList<WaiterHandler>());
		m_customers = Collections.synchronizedList(new ArrayList<CustomerHandler>());
		m_tables = Collections.synchronizedList(new ArrayList<Table>());
	}
	
	private void customerEnteredRestaurant(Message message) {
		Customer customer = message.get(0);
		print("Customer " + customer.getName() + " entered the restaurant.");
		synchronized (m_customers) {
			boolean found = false;
			for (CustomerHandler c : m_customers) {
				if (c.customer == customer) {
					found = true;
					break;
				}
			}
			
			if (!found) {	
				CustomerHandler c = new CustomerHandler(customer);
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
		print(waiter.getName() + " came to work today.");
		WaiterHandler w = new WaiterHandler(waiter);
		synchronized (m_waiters) {
			m_waiters.add(w);
		}
	}
	
	private class CustomerHandler {
		public Customer customer;
		public Table table;
		
		public CustomerHandler(Customer customer) {
			this.customer = customer;
		}
		
		public CustomerHandler(Customer customer, Table table) {
			this.customer = customer;
			this.table = table;
		}
	}
	
	private class WaiterHandler {
		public int numCustomers;
		public WaiterStateEnum state;
		public Waiter waiter;
		public Table table;
		
		public WaiterHandler(Waiter waiter) {
			this.waiter = waiter;
		}
		
		public WaiterHandler(Waiter waiter, Table table) {
			this.waiter = waiter;
			this.table = table;
		}
	}
	
	private enum WaiterStateEnum {
		Idle,
		Working
	}
	
	private enum CustomerStateEnum {
		Idle,
	}
}
