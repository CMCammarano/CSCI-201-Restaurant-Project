/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.restaurant.agent;

import core.agent.Agent;
import core.agent.Message;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author cmcammarano
 */
public class Host extends Agent {

	private final List<Waiter> m_waiters;
	private final List<Customer> m_customers;
	
	public Host(String name) {
		super(name);
		
		m_waiters = Collections.synchronizedList(new ArrayList<Waiter>());
		m_customers = Collections.synchronizedList(new ArrayList<Customer>());
	}
	
	private void customerEnteredRestaurant(Message message) {
		Customer customer = message.get(0);
		print("Customer " + customer.getName() + " entered the restaurant.");
	}
	
	@Override
	public boolean update() {
		return false;
	}
	
	public void addWaiter(Waiter waiter) {
		synchronized (m_waiters) {
			m_waiters.add(waiter);
		}
	}
}
