/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.restaurant;

import core.restaurant.agent.Cashier;
import core.restaurant.agent.Cook;
import core.restaurant.agent.Customer;
import core.restaurant.agent.Host;
import core.restaurant.agent.Waiter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author cmcammarano
 */
public class Restaurant {
	
	private static final int NUMBER_OF_TABLES = 5;
	private final Host m_host;
	private final Cook m_cook;
	private final Cashier m_cashier;
	private final List<Waiter> m_waiters;
	private final List<Customer> m_customers;
	private final List<Table> m_tables;
	private final HashMap<String, Float> m_menu;
	
	public Restaurant() {
		m_waiters = new ArrayList<Waiter>();
		m_customers = new ArrayList<Customer>();
		m_tables = new ArrayList<Table>();
		for (int i = 0; i < NUMBER_OF_TABLES; i++) {
			m_tables.add(new Table(i + 1, i, i));
		}
		
		m_menu = new HashMap<String, Float>();
		m_menu.put("steak", 10.99f);
		m_menu.put("chicken", 8.99f);
		m_menu.put("salad", 5.99f);
		m_menu.put("burger", 9.99f);
		m_menu.put("pizza", 7.99f);
		
		m_host = new Host("Host", m_tables, m_menu);
		m_host.startThread();
		
		m_cook = new Cook("Cook");
		m_cook.startThread();
		
		m_cashier = new Cashier("Cashier", m_menu);
		m_cashier.startThread();
	
		// FOR INITIAL TESTING
		AddWaiter("Waiter1");
		AddWaiter("Waiter2");
		AddCustomer("Customer1");
		AddCustomer("Customer2");
		AddCustomer("Customer3");
		AddCustomer("Customer4");
		AddCustomer("Customer5");
	}
	
	public void AddWaiter(String name) {
		Waiter waiter = new Waiter(name);
		waiter.setHost(m_host);
		waiter.setCashier(m_cashier);
		waiter.setCook(m_cook);
		waiter.startThread();
		
		m_host.addWaiter(waiter);
		m_waiters.add(waiter);
	}
	
	public List<Customer> getCustomers() { return m_customers; }
	public List<Waiter> getWaiters() { return m_waiters; }
	
	public void AddCustomer(String name) {
		Customer customer = new Customer(name);
		customer.setHost(m_host);
		customer.setCashier(m_cashier);
		customer.startThread();
		
		m_customers.add(customer);
		
		// Simulate interactions until GUI is added
		customer.sendMessage("becomeHungry");
	}
}
