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
import gui.agents.CookGUI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Semaphore;

/**
 *
 * @author cmcammarano
 */
public class Cook extends Agent {

	private CookGUI m_gui;
	private final Semaphore m_atDestination;
	private final List<Order> m_orders;
	
	public Cook(String name) {
		super(name);
		m_atDestination = new Semaphore(0, true);
		m_orders = Collections.synchronizedList(new ArrayList<Order>());
	}
	
	private void cookOrder(Order order) {
		order.setStatus(OrderStatusEnum.Cooking);
		
		doGoToRefrigerator();
		try {
			m_atDestination.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace(System.err);
		}
		
		doGrabFood(order);
		doCookFood();
		try {
			m_atDestination.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace(System.err);
		}
		
		print("Cooking " + order.getChoice() + ".");
	}
	
	private void callWaiter(Order order) {
		print("Calling " + order.getWaiter().getName() + " to pickup the order.");
		
		doPlating(order);
		try {
			m_atDestination.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		doGoToFoodArea(order);
		try {
			m_atDestination.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		order.setStatus(OrderStatusEnum.Finished);
		order.getWaiter().sendMessage("pickupOrder", new Message(order));
	}
	
	// Animations
	private void doGoToRefrigerator() {
		m_gui.doGoToRefrigerator();
	}
	
	private void doGrabFood(Order order) {
		m_gui.doGrabFood(order.getChoice());
	}
	
	private void doCookFood() {
		m_gui.doCooking();
	}
	
	private void doPlating(Order order) {
		m_gui.doGoToPlatingArea(order.getChoice());
	}
	
	private void doGoToFoodArea(Order order) {
		m_gui.doGoToFoodArea(order.getChoice());
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
	
	// Messages -- Animation
	public void atDestination() {
		m_atDestination.release();
	}
	
	public CookGUI getCookGUI() { return m_gui; }
	public void setCookGUI(CookGUI gui) { m_gui = gui; }
}
