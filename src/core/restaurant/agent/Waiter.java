/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.restaurant.agent;

import core.agent.Agent;

/**
 *
 * @author ColinCammarano
 */
public class Waiter extends Agent {

	private Host m_host;
	private Cashier m_cashier;
	
	public Waiter(String name) {
		super(name);
	}

	@Override
	public boolean update() {
		return false;
	}
	
	public Host getHost() { return m_host; }
	public void setHost(Host host) { m_host = host; }
	
	public Cashier getCashier() { return m_cashier; }
	public void setCashier(Cashier cashier) { m_cashier = cashier; }
}
