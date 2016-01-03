/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.agents;

import core.restaurant.Table;
import core.restaurant.agent.Customer;
import gui.RestaurantGUI;
import java.awt.Color;
import java.awt.Graphics2D;

/**
 *
 * @author cmcammarano
 */
public class CustomerGUI extends AgentGUI {

	private String m_choice;
	private boolean m_pause;
	private boolean m_isHungry;
	private boolean m_isPresent;

	private int m_xPos;
	private int m_yPos;
	private int m_xDestination;
	private int m_yDestination;
	
	private enum Command { noCommand, GoToWaitArea, GoToSeat, LeaveRestaurant };
	private Command m_command = Command.noCommand;
	
	private enum IconState { None, OrderPending, OrderReceived };
	private IconState m_iconState = IconState.None;
	
	private final Customer m_customer;
	private final RestaurantGUI m_gui;
	
	private static final int X_SCALE = 20;
	private static final int Y_SCALE = 20;
	
	public static final int X_WAIT_POS = 50;
	public static final int Y_WAIT_POS = 50;
	
	public CustomerGUI(Customer customer, RestaurantGUI gui) {
		m_customer = customer;
		m_gui = gui;
		
		m_pause = false;
		m_isHungry = false;
		m_isPresent = false;
		
		m_xPos = -40;
		m_yPos = -40;
		
		m_choice = "";
	}
	
	@Override
	public void updatePosition() {
		if(m_pause) {
			return;
		}
		
		if (m_xPos < m_xDestination) {
			m_xPos++;
		}
		
		else if (m_xPos > m_xDestination) {
			m_xPos--;
		}
		
		else if (m_yPos < m_yDestination) {
			m_yPos++;
		}
		
		else if (m_yPos > m_yDestination) {
			m_yPos--;
		}
		
		if (m_xPos == m_xDestination && m_yPos == m_yDestination) {
			
			if (m_command != Command.noCommand) {
				m_customer.atDestination();
			}
			
			if (m_command == Command.LeaveRestaurant) {
				m_isHungry = false;
				m_gui.setCustomerEnabled(m_customer);
			}
			
			m_command = Command.noCommand;
		}
	}

	@Override
	public void draw(Graphics2D graphics) {
		graphics.setColor(Color.GREEN);
		graphics.fillRect(m_xPos, m_yPos, X_SCALE, Y_SCALE);
		
		if(m_iconState == IconState.OrderPending) {
			graphics.setColor(Color.BLACK);
			graphics.drawString(m_choice + "?", m_xPos + X_SCALE, m_yPos + Y_SCALE);
		}
		
		if(m_iconState == IconState.OrderReceived) {
			graphics.setColor(Color.BLACK);
			graphics.drawString(m_choice, m_xPos + X_SCALE, m_yPos + Y_SCALE);
		}
	}

	@Override
	public boolean isPresent() {
		return m_isPresent;
	}
	
	public void setHungry() {
		m_isHungry = true;
		m_customer.sendMessage("becomeHungry");
		setPresent(true);
	}
	
	public boolean isHungry() {
		return m_isHungry;
	}

	public void setPresent(boolean present) {
		m_isPresent = present;
	}

	public void doGoToHost() {
		m_xDestination = X_WAIT_POS;
		m_yDestination = Y_WAIT_POS;
		
		m_command = Command.GoToWaitArea;
	}
	
	public void doGoToSeat(Table table) {		//later you will map seatnumber to table coordinates.
		m_xDestination = table.getPosX();
		m_yDestination = table.getPosY() - 50;

		m_command = Command.GoToSeat;
	}

	public void doExitRestaurant() {
		m_xDestination = -40;
		m_yDestination = -40;
		m_command = Command.LeaveRestaurant;
	}
	
	public void madeOrder(String choice) {
		switch(choice) {
			case "steak":
				m_choice = "st";
				break;
			case "chicken":
				m_choice = "ch";
				break;
			case "pizza":
				m_choice = "pi";
				break;
			case "salad":
				m_choice = "sa";
				break;
		}
		
		m_iconState = IconState.OrderPending;
	}
	
	public void gotFood() {
		m_iconState = IconState.OrderReceived;
	}
	
	public void finishedFood() {
		m_iconState = IconState.None;
	}
	
	public void pause() {
		m_pause = true;
	}
	
	public void resume() {
		m_pause = false;
	}
}
