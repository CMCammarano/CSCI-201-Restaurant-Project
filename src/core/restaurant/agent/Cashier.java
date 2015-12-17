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
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author cmcammarano
 */
public class Cashier extends Agent {

	private final List<Check> m_checks;
	private final HashMap<String, Float> m_menu;
	
	public Cashier(String name, HashMap<String, Float> menu) {
		super(name);
		m_checks = Collections.synchronizedList(new ArrayList<Check>());
		m_menu = menu;
	}	

	private void computeCustomerCheck(Check check) {
		print("Tabulating " + check.getCustomer().getName() + "'s check.");
		check.setStatus(CheckStatusEnum.Sent);
		check.getWaiter().sendMessage("pickupCheck", new Message(check));
	}
	
	private void calculateChange(Check check) {
		print("Calculating change for " + check.getCustomer().getName() + ".");
		check.setStatus(CheckStatusEnum.Paid);
		check.getCustomer().sendMessage("receiveChange", new Message(check.getChange()));
	}
	
	@Override
	public boolean update() {
		synchronized (m_checks) {
			for (Check c : m_checks) {
				if (c.getStatus() == CheckStatusEnum.Created) {
					computeCustomerCheck(c);
					return true;
				}
			}
		}
		
		synchronized (m_checks) {
			for (Check c : m_checks) {
				if (c.getStatus() == CheckStatusEnum.Received) {
					calculateChange(c);
					return true;
				}
			}
		}
		
		synchronized (m_checks) {
			for (Check c : m_checks) {
				if (c.getStatus() == CheckStatusEnum.Paid) {
					m_checks.remove(c);
					return true;
				}
			}
		}
		
		return false;
	}
	
	public void computeCheck(Message message) {
		Waiter waiter = message.get(0);
		Customer customer = message.get(1);
		String choice = message.get(2);
		m_checks.add(new Check(waiter, customer, choice));
		
		print("Computing " + customer.getName() + "'s check for his/her " + choice + ".");
		stateChanged();
	}
	
	public void payForMeal(Message message) {
		Customer customer = message.get(0);
		float amountPaid = message.get(1);
		for (Check c : m_checks) {
			if (c.getCustomer() == customer) {
				c.setStatus(CheckStatusEnum.Received);
				float cost = m_menu.get(c.getChoice());
				c.setChange(amountPaid - cost);
			}
		}
		
		print(customer.getName() + " is paying for his/her meal.");
		stateChanged();
	}
}
