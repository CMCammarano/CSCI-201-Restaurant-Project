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
	
	public Host(String name, List<Table> tables) {
		super(name);
		
		m_waiters = Collections.synchronizedList(new ArrayList<WaiterHandler>());
		m_customers = Collections.synchronizedList(new ArrayList<CustomerHandler>());
		m_tables = Collections.synchronizedList(tables);
	}
	
	/* PRIVATE MEMBER METHODS */
	private void assignCustomerToTable(CustomerHandler customer) {
		Table unassignedTable = null;
		synchronized (m_tables) {
			for (Table table : m_tables) {
				if (table.getOccupant() == null) {
					unassignedTable = table;
					break;
				}
			}
		}
		
		if (unassignedTable != null) {
			if (m_waiters.size() > 0) {			
				WaiterHandler waiter = m_waiters.get(0);
				synchronized (m_waiters) {
					for (WaiterHandler w : m_waiters) {
						if (waiter.numCustomers > w.numCustomers) {
							waiter = w;
						}
					}
				}
				
				print("Assigning " + customer.customer.getName() + " to waiter " + waiter.waiter.getName() + " at " + unassignedTable.toString());
				
				customer.state = CustomerStateEnum.Eating;
				unassignedTable.setOccupant(customer.customer);
				waiter.state = WaiterStateEnum.Working;
				waiter.table = unassignedTable;
				waiter.numCustomers++;
				waiter.waiter.sendMessage("seatCustomer", new Message(customer.customer, unassignedTable));
			}
		}
		
		else {
			print("Cannot seat " + customer.customer.getName());
			customer.state = CustomerStateEnum.Idle;
			customer.customer.sendMessage("restaurantIsFull");
		}
	}
	
	@Override
	public boolean update() {
		synchronized (m_customers) {
			for (CustomerHandler c : m_customers) {
				if (c.state == CustomerStateEnum.Idle) {
					assignCustomerToTable(c);
					return true;
				}
			}
		}
		return false;
	}
	
	// Messages -- Customer
	public void customerEnteredRestaurant(Message message) {
		Customer customer = message.get(0);
		print("Customer " + customer.getName() + " entered the restaurant.");
		
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
		
		stateChanged();
	}
	
	public void customerLeftRestaurant(Message message) {
		Customer customer = message.get(0);
		print("Customer " + customer.getName() + " left the restaurant.");
		
		for (CustomerHandler cust : m_customers) {
			if (cust.customer == customer) {
				m_customers.remove(cust);
				break;
			}
		}
		
		for (Table table : m_tables) {
			if (table.getOccupant() == customer) {
				table.removeOccupant();
			}
		}
		
		stateChanged();
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
		public CustomerStateEnum state;
		
		public CustomerHandler(Customer customer) {
			this.customer = customer;
			state = CustomerStateEnum.Idle;
		}
		
		public CustomerHandler(Customer customer, Table table) {
			this.customer = customer;
			this.table = table;
			state = CustomerStateEnum.Idle;
		}
	}
	
	private class WaiterHandler {
		public Waiter waiter;
		public Table table;
		public WaiterStateEnum state;
		public int numCustomers;
		
		public WaiterHandler(Waiter waiter) {
			this.waiter = waiter;
			state = WaiterStateEnum.Working;
		}
		
		public WaiterHandler(Waiter waiter, Table table) {
			this.waiter = waiter;
			this.table = table;
			state = WaiterStateEnum.Working;
		}
	}
	
	private enum WaiterStateEnum {
		Idle,
		Working
	}
	
	private enum CustomerStateEnum {
		Idle,
		Eating
	}
}
