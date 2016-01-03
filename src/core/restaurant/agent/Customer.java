/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.restaurant.agent;

import core.agent.Agent;
import core.agent.Message;
import core.restaurant.Table;
import gui.agents.CustomerGUI;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.Semaphore;

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
	private HashMap<String, Float> m_menu;
	
	private final Semaphore m_atDestination;
	
	// GUI
	private CustomerGUI m_gui;
	
	/* CONSTRUCTOR */
	public Customer(String name) {
		super(name);
		
		Random random = new Random();
		m_hunger = 5;
		m_money = 20.0f;
		m_choice = "";
		m_state = CustomerStateEnum.Idle;
		m_event = EventEnum.None;
		m_atDestination = new Semaphore(0, true);
	}
	
	public Customer(String name, int hunger, float money) {
		super(name);
		
		Random random = new Random();
		m_hunger = hunger;
		m_money = money;
		m_choice = "";
		m_state = CustomerStateEnum.Idle;
		m_event = EventEnum.None;
		m_atDestination = new Semaphore(0, true);
	}
	
	/* PRIVATE MEMBER METHODS */
	private void goToRestaurant() {
		print("Going to the restaurant.");
		print("I have: $" + getMoney());
		
		doGoToHost();
		try {
			m_atDestination.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace(System.err);
		}
		
		if (m_money < 5.99f) {
			print("I can't afford anything here.");
			m_state = CustomerStateEnum.Leaving;
			m_event = EventEnum.Done;
		}
		
		else {
			m_state = CustomerStateEnum.WaitingInRestaurant;
			m_host.sendMessage("customerEnteredRestaurant", new Message(this));
		}
	}
	
	private void leaveRestaurant() {
		print("Leaving the restaurant.");
		m_state = CustomerStateEnum.Idle;
		m_choice = "";
		
		doExitRestaurant();
		try {
			m_atDestination.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace(System.err);
		}
			
		m_host.sendMessage("customerLeftRestaurant", new Message(this));
	}
	
	private void followWaiter() {
		m_state = CustomerStateEnum.Seated;
		
		doFollowWaiter();
		try {
			m_atDestination.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace(System.err);
		}
	}
	
	private void chooseFood() {
		m_state = CustomerStateEnum.Choosing;
		
		while (m_choice.equals("")) {
			Random random = new Random();
			int decision = random.nextInt(5);
			switch (decision) {
				case 0:
					if (m_money > m_menu.get("steak")) {
						m_choice = "steak";
					}
					break;			
				case 1:
					if (m_money > m_menu.get("chicken")) {
						m_choice = "chicken";
					}
					break;
				case 2:
					if (m_money > m_menu.get("salad")) {
						m_choice = "salad";
					}
					break;
				case 3:
					if (m_money > m_menu.get("burger")) {
						m_choice = "burger";
					}
					break;
				case 4:
					if (m_money > m_menu.get("pizza")) {
						m_choice = "pizza";
					}
					break;
			}
		}
		
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
		m_state = CustomerStateEnum.AskForCheck;
		m_waiter.sendMessage("askForCheck", new Message(this));
	}
	
	private void payForMeal() {
		m_state = CustomerStateEnum.Leaving;
		float amountToPay = 0;
		
		Random random = new Random();
		int decision = random.nextInt(2);
		if (decision == 1 && m_money > 15.0f) {
			amountToPay = 15.0f;
		}
		
		else {
			amountToPay = m_menu.get(m_choice);
		}
		m_money -= amountToPay;
		m_cashier.sendMessage("payForMeal", new Message(this, amountToPay));
	}
	
	// Animations
	private void doGoToHost() {
		m_gui.doGoToHost();
	}
	
	private void doExitRestaurant() {
		m_gui.doExitRestaurant();
	}
	
	private void doFollowWaiter() {
		m_gui.doGoToSeat(m_table);
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
		
		if (m_state == CustomerStateEnum.AskForCheck && m_event == EventEnum.GotCheck) {
			payForMeal();
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
		m_menu = message.get(2);
		
		print(m_waiter.getName() + " took me to " + m_table.toString());
		m_event = EventEnum.BeingSeated;
		stateChanged();
	}
	
	public void takeOrder() {
		print(m_waiter.getName() + " is taking my order.");
		m_event = EventEnum.ChoseFood;
		stateChanged();
	}
	
	public void receiveFood() {
		print(m_waiter.getName() + " brought me my " + m_choice);
		m_event = EventEnum.GotFood;
		stateChanged();
	}
	
	public void receiveCheck() {
		print(m_waiter.getName() + " brought me my check.");
		m_event = EventEnum.GotCheck;
		stateChanged();
	}
	
	// Messages -- Cashier
	public void receiveChange(Message message) {
		float change = message.get(0);
		m_money += change;
		m_event = EventEnum.Done;
		
		print("Received $" + change + " from the cashier.");
		stateChanged();
	}
	
	// Messages -- Animation
	public void atDestination() {
		m_atDestination.release();
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

	public CustomerGUI getCustomerGUI() { return m_gui; }
	public void setCustomerGUI(CustomerGUI gui) { m_gui = gui; }
	
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