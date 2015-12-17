/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.restaurant;

import core.restaurant.agent.Customer;
import core.restaurant.agent.Waiter;

/**
 *
 * @author cmcammarano
 */
public class Check {
	private Waiter m_waiter;
	private Customer m_customer;
	private String m_choice;
	private float m_change;
	private CheckStatusEnum m_status;

	public Check(Waiter waiter, Customer customer, String choice) {
		m_waiter = waiter;
		m_customer = customer;
		m_choice = choice;
		m_change = 0.0f;
		m_status = CheckStatusEnum.Created;
	}
	
	public Waiter getWaiter() { return m_waiter; }
	public void setWaiter(Waiter waiter) { m_waiter = waiter; }
	
	public Customer getCustomer() { return m_customer; }
	public void setCustomer(Customer customer) { m_customer = customer; }
	
	public String getChoice() { return m_choice; }
	public void setChoice(String choice) { m_choice = choice; }
	
	public float getChange() { return m_change; }
	public void setChange(float change) { m_change = change; }
	
	public CheckStatusEnum getStatus() { return m_status; }
	public void setStatus(CheckStatusEnum status) { m_status = status; }
	
	public enum CheckStatusEnum {
		Created,
		Sent,
		Received,
		Paid
	}
}