/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.restaurant.agent;

import core.agent.Agent;
import core.agent.Message;
import core.restaurant.Order;
import core.restaurant.Order.OrderStatusEnum;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author cmcammarano
 */
public class Cook extends Agent {

	private final List<Order> m_orders;
	
	public Cook(String name) {
		super(name);
		
		m_orders = Collections.synchronizedList(new ArrayList<Order>());
	}
	
	private void cookOrder(Order order) {
		order.setStatus(OrderStatusEnum.Cooking);
		print("Cooking " + order.getChoice() + ".");
	}
	
	private void callWaiter(Order order) {
		print("Calling " + order.getWaiter().getName() + " to pickup the order.");
		order.setStatus(OrderStatusEnum.Finished);
		order.getWaiter().sendMessage("pickupOrder", new Message(order));
	}

	@Override
	public boolean update() {
		synchronized (m_orders) {
			for (Order o : m_orders) {				
				if (o.getStatus() == OrderStatusEnum.Started) {
					cookOrder(o);
					return true;
				}
			}
		}
		
		synchronized (m_orders) {
			for (Order o : m_orders) {	
				if (o.getStatus() == OrderStatusEnum.Cooking) {
					callWaiter(o);
					return true;
				}
			}
		}
		return false;
	}
	
	public void takeOrder(Message message) {
		Order order = message.get(0);
		print("Cooking an order of " + order.getChoice() + " for " + order.getCustomer().getName() + " served by waiter " + order.getWaiter().getName() + ".");
		m_orders.add(order);
		
		stateChanged();
	}
}
