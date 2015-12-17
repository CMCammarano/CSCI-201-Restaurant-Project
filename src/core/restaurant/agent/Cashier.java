/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.restaurant.agent;

import core.agent.Agent;
import core.agent.Message;
import core.restaurant.Check;
import core.restaurant.Check.CheckStatusEnum;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author cmcammarano
 */
public class Cashier extends Agent {

	private final List<Check> m_checks;
	
	public Cashier(String name) {
		super(name);
		m_checks = Collections.synchronizedList(new ArrayList<Check>());
	}	

	private void computeCustomerCheck(Check c) {
		print("Tabulating " + c.getCustomer().getName() + "'s check.");
		c.setStatus(CheckStatusEnum.Sent);
		c.getWaiter().sendMessage("pickupCheck", new Message(c));
	}
	
	@Override
	public boolean update() {
		synchronized (m_checks) {
			for (Check c : m_checks) {
				if (c.getStatus() == CheckStatusEnum.Created) {
					computeCustomerCheck(c);
				}
			}
		}
		
		return false;
	}
	
	public void computeCheck(Message message) {
		Waiter waiter = message.get(0);
		Customer customer = message.get(1);
		String choice = message.get(2);
		synchronized (m_checks) {
			m_checks.add(new Check(waiter, customer, choice));
		}
		
		print("Computing " + customer.getName() + "'s check for his/her " + choice + ".");
		stateChanged();
	}
}
