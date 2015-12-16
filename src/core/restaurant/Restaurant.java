/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.restaurant;

import core.restaurant.agent.Cashier;
import core.restaurant.agent.Cook;
import core.restaurant.agent.Host;
import core.restaurant.agent.Waiter;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author cmcammarano
 */
public class Restaurant {
	
	private final Host m_host;
	private final Cook m_cook;
	private final Cashier m_cashier;
	private final List<Waiter> m_waiters;
	
	public Restaurant() {
		m_host = new Host("Host");
		m_cook = new Cook("Cook");
		m_cashier = new Cashier("Cashier");
		
		m_waiters = new ArrayList<Waiter>();
	}
	
	public void AddWaiter(String name) {
		Waiter waiter = new Waiter(name);
		waiter.setHost(m_host);
		waiter.setCashier(m_cashier);
		m_waiters.add(waiter);
	}
}
