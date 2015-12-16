/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.restaurant.agent;

import core.agent.Agent;
import core.agent.Message;
import core.restaurant.Table;
import java.util.Random;

/**
 *
 * @author cmcammarano
 */
public class Customer extends Agent {
	
	/* PRIVATE MEMBER VARIABLES */
	private int m_hunger;
	private float m_money;
	private String m_choice;
	private CustomerStateEnum m_state;
	private EventEnum m_event;
	private Host m_host;
	private Waiter m_waiter;
	private Cashier m_cashier;
	private Table m_table;
	
	/* CONSTRUCTOR */
	public Customer(String name) {
		super(name);
		
		Random random = new Random();
		m_hunger = 5;
		m_money = 20.0f;
		m_choice = "steak";
		m_state = CustomerStateEnum.Idle;
		m_event = EventEnum.None;
	}
	
	public Customer(String name, int hunger, float money) {
		super(name);
		
		Random random = new Random();
		m_hunger = hunger;
		m_money = money;
		m_choice = "steak";
		m_state = CustomerStateEnum.Idle;
		m_event = EventEnum.None;
	}
	
	/* PRIVATE MEMBER METHODS */
	private void goToRestaurant() {
		print("Going to the restaurant.");
		print("I have: $" + getMoney());
		if (m_money < 5.99f) {
			print("I can't afford anything here.");
			m_state = CustomerStateEnum.Leaving;
			m_event = EventEnum.Done;
		}
		
		else {
			//m_gui.doGoToHost();
			m_state = CustomerStateEnum.WaitingInRestaurant;
			m_host.sendMessage("customerEnteredRestaurant", new Message(this));
		}
	}
	
	private void leaveRestaurant() {
		print("Leaving the restaurant.");
		m_state = CustomerStateEnum.Idle;
		m_host.sendMessage("customerLeftRestaurant", new Message(this));
	}
	
	private void followWaiter() {
		m_state = CustomerStateEnum.Seated;
	}
	
	private void chooseFood() {
		m_state = CustomerStateEnum.Choosing;
		m_waiter.sendMessage("madeChoice", new Message(this));
	}
	
	private void makeOrder() {
		m_state = CustomerStateEnum.Ordered;
		print("I would like to order a " + m_choice);
		m_waiter.sendMessage("placeOrder", new Message(this, m_choice));
	}
	
	private void eatFood() {
		m_state = CustomerStateEnum.Eating;
		m_event = EventEnum.DoneEating;
	}
	
	private void askForCheck() {
		m_state = CustomerStateEnum.Paying;
		m_waiter.sendMessage("askForCheck", new Message(this, m_choice));
	}
	
	/* PUBLIC MEMBER METHODS */
	@Override
	public boolean update() {
		
		if (m_state == CustomerStateEnum.Idle && m_event == EventEnum.GotHungry) {
			goToRestaurant();
			return true;
		}
		
		if (m_state == CustomerStateEnum.WaitingInRestaurant && m_event == EventEnum.BeingSeated) {
			followWaiter();
			return true;
		}
		
		if (m_state == CustomerStateEnum.Seated && m_event == EventEnum.BeingSeated) {
			chooseFood();
			return true;
		}
		
		if (m_state == CustomerStateEnum.Choosing && m_event == EventEnum.ChoseFood) {
			makeOrder();
			return true;
		}
		
		if (m_state == CustomerStateEnum.Ordered && m_event == EventEnum.GotFood) {
			eatFood();
			return true;
		}
		
		if (m_state == CustomerStateEnum.Eating && m_event == EventEnum.DoneEating) {
			askForCheck();
			return true;
		}
			
		if (m_state == CustomerStateEnum.Leaving && m_event == EventEnum.Done) {
			leaveRestaurant();
			return false;
		}
		return false;
	}
	
	public void becomeHungry() {
		print("I am hungry and want to eat.");
		m_event = EventEnum.GotHungry;
		stateChanged();
	}
	
	// Messages -- Host
	public void restaurantIsFull() {
		print("Restaurant is full.");
		m_state = CustomerStateEnum.Leaving;
		m_event = EventEnum.Done;
		stateChanged();
	}
	
	// Messages -- Waiter
	public void sitAtTable(Message message) {
		m_waiter = message.get(0);
		m_table = message.get(1);
		print(m_waiter.getName() + " took us to " + m_table.toString());
		m_event = EventEnum.BeingSeated;
		stateChanged();
	}
	
	public void takeOrder() {
		print(m_waiter.getName() + " is taking our order.");
		m_event = EventEnum.ChoseFood;
		stateChanged();
	}
	
	public void receiveFood() {
		print(m_waiter.getName() + " brought us our " + m_choice);
		m_event = EventEnum.GotFood;
		stateChanged();
	}
	
	public void receiveCheck() {
		print(m_waiter.getName() + " brought us our check.");
		m_event = EventEnum.GotCheck;
		stateChanged();
	}
	
	// Messages -- Cashier
	public void receiveChange(Message message) {
		stateChanged();
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
		Seated,
		Choosing,
		Ordered,
		Eating,
		DoneEating,
		AskForCheck,
		Paying,
		Leaving
	}
	
	private enum EventEnum {
		None,
		GotHungry,
		BeingSeated,
		ChoseFood,
		GotFood,
		DoneEating,
		GotCheck,
		ReadyToPay,
		ReadyToLeave,
		Done
	}
}