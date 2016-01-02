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

	private String name = "";
	private boolean pause = false;
	private boolean isPresent = false;
	private boolean isHungry = false;

	private int xPos;
	private int yPos;
	private int xDestination, yDestination;
	
	private enum Command {noCommand, GoToWaitArea, GoToSeat, LeaveRestaurant};
	private Command command=Command.noCommand;
	
	private enum IconState { None, OrderPending, OrderReceived };
	private IconState iconState = IconState.None;
	
	private final Customer m_customer;
	private final RestaurantGUI m_gui;
	
	private static final int X_SCALE = 20;
	private static final int Y_SCALE = 20;
	
	public static int X_WAIT_POS = 50;
	public static int Y_WAIT_POS = 50;
	
	public CustomerGUI(Customer customer, RestaurantGUI gui) {
		m_customer = customer;
		m_gui = gui;
	}
	
	@Override
	public void updatePosition() {
		if(pause) {
			return;
		}
		
		if (xPos < xDestination) {
			xPos++;
		}
		
		else if (xPos > xDestination) {
			xPos--;
		}
		
		if (yPos < yDestination) {
			yPos++;
		}
		
		else if (yPos > yDestination) {
			yPos--;
		}
		
		if (xPos == xDestination && yPos == yDestination) {
			if (command==Command.GoToSeat) {
				//m_customer.msgAnimationFinishedGoToSeat();
			}
			
			else if (command==Command.LeaveRestaurant) {
				//m_customer.msgAnimationFinishedLeaveRestaurant();
				System.out.println("about to call gui.setCustomerEnabled(agent);");
				isHungry = false;
				m_gui.setCustomerEnabled(m_customer);
			}
			
			command=Command.noCommand;
		}
	}

	@Override
	public void draw(Graphics2D graphics) {
		graphics.setColor(Color.GREEN);
		graphics.fillRect(xPos, yPos, X_SCALE, Y_SCALE);
		
		if(iconState == IconState.OrderPending) {
			graphics.setColor(Color.BLACK);
			graphics.drawString(name + "?", xPos + X_SCALE, yPos + Y_SCALE);
		}
		
		if(iconState == IconState.OrderReceived) {
			graphics.setColor(Color.BLACK);
			graphics.drawString(name, xPos + X_SCALE, yPos + Y_SCALE);
		}
	}

	@Override
	public boolean isPresent() {
		return isPresent;
	}
	public void setHungry() {
		isHungry = true;
		m_customer.sendMessage("becomeHungry");
		setPresent(true);
	}
	public boolean isHungry() {
		return isHungry;
	}

	public void setPresent(boolean p) {
		isPresent = p;
	}

	public void doGoToHost() {
		xDestination = X_WAIT_POS;
		yDestination = Y_WAIT_POS;
		
		command = Command.GoToWaitArea;
	}
	
	public void doGoToSeat(Table table) {		//later you will map seatnumber to table coordinates.
		xDestination = table.getPosX();
		yDestination = table.getPosY();
		
		//xDestination = xTable;
		//yDestination = yTable;

		command = Command.GoToSeat;
	}

	public void doExitRestaurant() {
		xDestination = -40;
		yDestination = -40;
		command = Command.LeaveRestaurant;
	}
	
	public void madeOrder(String choice) {
		switch(choice) {
			case "steak":
				name = "st";
				break;
			case "chicken":
				name = "ch";
				break;
			case "pizza":
				name = "pi";
				break;
			case "salad":
				name = "sa";
				break;
		}
		
		iconState = IconState.OrderPending;
	}
	
	public void gotFood() {
		iconState = IconState.OrderReceived;
	}
	
	public void finishedFood() {
		iconState = IconState.None;
	}
	
	public void pause() {
		pause = true;
	}
	
	public void resume() {
		pause = false;
	}
}
