/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.agents;

import core.restaurant.Table;
import core.restaurant.agent.Waiter;
import java.awt.Color;
import java.awt.Graphics2D;

/**
 *
 * @author cmcammarano
 */
public class WaiterGUI extends AgentGUI {

	private final Waiter m_waiter;
	
	private boolean m_acting;
	private boolean m_paused;
	private boolean m_hasFood;
	
	private String m_choice;
	
	// Coordinates
	private int m_xPos;
	private int m_yPos;
	
	private int m_xDestination;
	private int m_yDestination;
	
	public int m_xTable;
	public int m_yTable;
	
	private final int m_xWaitPos;
	private final int m_yWaitPos;
	
	private static final int X_COOK_POS = 600;
	private static final int Y_COOK_POS = 600;
	
	private static final int X_CUSTOMER_WAITING_AREA = 50;
	private static final int Y_CUSTOMER_WAITING_AREA = 50;
	
	private static final int X_CASHIER_POS = 600;
	private static final int Y_CASHIER_POS = 50;
	
	private static final int X_SCALE = 20;
	private static final int Y_SCALE = 20;
	
	public WaiterGUI(Waiter waiter, int x, int y) {
		m_waiter = waiter;
		
		m_acting = false;
		m_paused = false;
		m_hasFood = false;
		
		m_xPos = x;
		m_yPos = y;
		
		m_xDestination = x;
		m_yDestination = y;
		
		m_xWaitPos = x;
		m_yWaitPos = y;
		
		m_xTable = 0;
		m_yTable = 0;
	}
	
	@Override
	public void updatePosition() {
		
		if (m_paused) {
			return;
		}
		
		if (m_xPos < m_xDestination) {
			m_xPos++;
		}
		
		else if (m_xPos > m_xDestination) {
			m_xPos--;
		}
		
		if (m_yPos < m_yDestination) {
			m_yPos++;
		}
		
		else if (m_yPos > m_yDestination) {
			m_yPos--;
		}
		
		// At destination
		if (m_acting == true && m_xPos == m_xDestination && m_yPos == m_yDestination) {
			m_waiter.atDestination();
			m_acting = false;
		}
	}

	@Override
	public void draw(Graphics2D graphics) {
		graphics.setColor(Color.MAGENTA);
		graphics.fillRect(m_xPos, m_yPos, X_SCALE, Y_SCALE);
		
		if(m_hasFood) {
			graphics.setColor(Color.BLACK);
			graphics.drawString(m_choice, m_xPos + X_SCALE, m_yPos + Y_SCALE);
		}
	}

	@Override
	public boolean isPresent() {
		return true;
	}
	
	// Pausing and resuming
	public void pause() {
		m_paused = true;
	}
	
	public void resume() {
		m_paused = false;
	}
	
	// Animations
	public void doGoToCustomer() {
		m_xDestination = X_CUSTOMER_WAITING_AREA + X_SCALE;
		m_yDestination = Y_CUSTOMER_WAITING_AREA - Y_SCALE;
		
		m_acting = true;
	}
	
	public void doTakeCustomerToTable(Table table) {
		m_xTable = table.getPosX();
		m_yTable = table.getPosY();
		
		m_xDestination = m_xTable + X_SCALE;
		m_yDestination = m_yTable + Y_SCALE;
		
		m_acting = true;
	}
	
	public void doGoToWaitingArea() {
		m_xDestination = m_xWaitPos;
		m_yDestination = m_yWaitPos;
		
		m_acting = true;
	}
	
	public void doTakeCustomerOrder(Table table) {
		m_xTable = table.getPosX();
		m_yTable = table.getPosY();
		
		m_xDestination = m_xTable + X_SCALE;
		m_yDestination = m_yTable - Y_SCALE;
		
		m_acting = true;
	}
	
	public void doSendOrderToCook() {
		m_xDestination = X_COOK_POS;
		m_yDestination = Y_COOK_POS - 2 * Y_SCALE;
		
		m_acting = true;
	}
	
	public void doBringFoodToCustomer(String choice, Table table) {
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
		
		m_xTable = table.getPosX();
		m_yTable = table.getPosY();
		
		m_xDestination = m_xTable + X_SCALE;
		m_yDestination = m_yTable - Y_SCALE;
		
		m_hasFood = true;
		m_acting = true;
	}
	
	public void doGetCheckFromCashier() {
		m_xDestination = X_CASHIER_POS;
		m_yDestination = Y_CASHIER_POS + Y_SCALE;
		
		m_acting = true;
	}
	
	public void doBringCheckToCustomer(Table table) {
		m_xTable = table.getPosX();
		m_yTable = table.getPosY();
		
		m_xDestination = m_xTable + X_SCALE;
		m_yDestination = m_yTable - Y_SCALE;
		
		m_acting = true;
	}
}
