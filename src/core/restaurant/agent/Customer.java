/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.restaurant.agent;

import core.agent.Agent;
import core.agent.Message;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author cmcammarano
 */
public class Customer extends Agent {
	
	/* PRIVATE MEMBER VARIABLES */
	private static final int HUNGER_BOUNDS = 6;
	private static final float MONEY_BOUNDS = 100.0f;

	private int m_hunger;
	private float m_money;
	private CustomerStateEnum m_state;
	private EventEnum m_event;
	private Host m_host;
	private Waiter m_waiter;
	private Cashier m_cashier;
	
	/* CONSTRUCTOR */
	public Customer(String name) {
		super(name);
		
		Random random = new Random();
		m_money = random.nextFloat() * MONEY_BOUNDS;
		m_hunger = random.nextInt(HUNGER_BOUNDS);
		m_state = CustomerStateEnum.Idle;
	}
	
	/* PRIVATE MEMBER METHODS */
	private void goToRestaurant() {
		print("Going to the restaurant.");
		print("I have: $" + getMoney());
		if (m_money < 5.99f) {
			print("I can't afford anything here.");
			m_state = CustomerStateEnum.Leaving;
			m_event = EventEnum.DoneLeaving;
		}
		
		else {
			//m_gui.doGoToHost();
			m_host.sendMessage("customerEnteredRestaurant", new Message(this));
		}
	}
	
	private void leaveRestaurant() {
		print("Leaving the restaurant.");
		m_host.sendMessage("customerLeftRestaurant", new Message(this));
	}
	
	// Messages -- Host
	private void restaurantIsFull() {
		print("Restaurant is full.");
		m_state = CustomerStateEnum.Leaving;
	}
	
	// Messages -- Waiter
	private void sitAtTable(Message message) {
		
	}
		
	private void decideFood() {
		
	}
	
	private void reorder(Message message) {
		
	}
	
	private void receiveFood(Message message) {
		
	}
	
	private void receiveCheck() {
		
	}
	
	// Messages -- Cashier
	private void receiveChange(Message message) {
		
	}
	
	/* PUBLIC MEMBER METHODS */
	@Override
	public boolean update() {
		
		if (m_state == CustomerStateEnum.Idle && m_event == EventEnum.GotHungry ){
			m_state = CustomerStateEnum.WaitingInRestaurant;
			goToRestaurant();
			return true;
		}
		return false;
	}
	
	/* ACCESSORS AND MUTATORS */
	public float getHunger() { return m_hunger; }
	public void setHunger(int hunger) { m_hunger = hunger; }
	
	public float getMoney() { return m_money; }
	public void setMoney(float money) { m_money = money; }
	
	public Host getHost() { return m_host; }
	public void setHost(Host host) { m_host = host; }
	
	public Waiter getWaiter() { return m_waiter; }
	public void setWaiter(Waiter waiter) { m_waiter = waiter; }
	
	public Cashier getCashier() { return m_cashier; }
	public void setCashier(Cashier cashier) { m_cashier = cashier; }
	
	/* ENUMERATIONS */
	private enum CustomerStateEnum {
		Idle,
		WaitingInRestaurant,
		BeingSeated,
		Seated,
		Choosing,
		Ordering,
		Eating,
		DoneEating,
		AskForCheck,
		Paying,
		Leaving
	}
	
	private enum EventEnum {
		None,
		GotHungry,
		FollowWaiter,
		Seated,
		ChoosingFood,
		ChoseFood,
		ReorderingFood,
		ReorderedFood,
		GotFood,
		DoneEating,
		GotCheck,
		ReadyToPay,
		ReadyToLeave,
		DoneLeaving
	}
}