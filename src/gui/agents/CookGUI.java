/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.agents;

import core.restaurant.agent.Cook;
import java.awt.Color;
import java.awt.Graphics2D;

/**
 *
 * @author cmcammarano
 */
public class CookGUI extends AgentGUI {
	
	private final Cook m_cook;
	
	private boolean m_paused;
	private boolean m_isActing;
	private boolean m_hasFood;
	
	private int m_xPos;
	private int m_yPos;
	private int m_xDestination;
	private int m_yDestination;
	private String m_choice;
	
	private static final int X_SCALE = 20;
	private static final int Y_SCALE = 20;
	private static final int X_WAIT_POS = 600;
	private static final int Y_WAIT_POS = 600;
	private static final int X_REFRIGERATOR = 600;
	private static final int Y_REFRIGERATOR = 650;
	private static final int X_GRILL = 700;
	private static final int Y_GRILL = 620;
	private static final int X_PLATING_AREA = 500;
	private static final int Y_PLATING_AREA = 620;
	
	public CookGUI(Cook cook) {
		m_cook = cook;
		m_xPos = X_WAIT_POS;
		m_yPos = Y_WAIT_POS;
		m_xDestination = X_WAIT_POS;
		m_yDestination = Y_WAIT_POS;
	}

	@Override
	public void updatePosition() {
		if(m_paused) {
			return;
		}
		
		if (m_xPos < m_xDestination)
			m_xPos++;
		
		else if (m_xPos > m_xDestination)
			m_xPos--;

		if (m_yPos < m_yDestination)
			m_yPos++;
			
		else if (m_yPos > m_yDestination)
			m_yPos--;

		// At destination
		if (m_isActing == true && m_xPos == m_xDestination && m_yPos == m_yDestination) {// & (xDestination == xTable + X_SCALE) & (yDestination == yTable - Y_SCALE)) {
			m_cook.atDestination();
			m_isActing = false;
		}
	}

	@Override
	public void draw(Graphics2D graphics) {
		graphics.setColor(Color.BLUE);
		graphics.fillRect(m_xPos, m_yPos, X_SCALE, Y_SCALE);
	}

	@Override
	public boolean isPresent() {
		return true;
	}
	
	public void doGrabFood(String choice) {
		switch(choice) {
			case "steak":
				m_choice = "st?";
				break;
			case "chicken":
				m_choice = "ch?";
				break;
			case "pizza":
				m_choice = "pi?";
				break;
			case "salad":
				m_choice = "sa?";
				break;
		}
		
		m_hasFood = true;
	}
	
	public void doGoToPlatingArea(String choice) {
		switch(choice) {
		case "steak":
			m_choice = "st!";
			break;
		case "chicken":
			m_choice = "ch!";
			break;
		case "pizza":
			m_choice = "pi!";
			break;
		case "salad":
			m_choice = "sa!";
			break;
		}
		
		m_hasFood = true;
		
		m_xDestination = X_PLATING_AREA + X_SCALE;
		m_yDestination = Y_PLATING_AREA + Y_SCALE;
		m_isActing = true;
	}
	
	public void doCooking() {
		m_xDestination = X_GRILL - X_SCALE;
		m_yDestination = Y_GRILL + Y_SCALE;
		
		m_hasFood = true;
		
		m_isActing = true;
	}
	
	public void doGoToRefrigerator() {
		m_xDestination = X_REFRIGERATOR;
		m_yDestination = Y_REFRIGERATOR - Y_SCALE;
		m_isActing = true;
	}
	
	public void doGoToFoodArea(String choice) {
		switch(choice) {
		case "steak":
			m_choice = "st!!";
			break;
		case "chicken":
			m_choice = "ch!!";
			break;
		case "pizza":
			m_choice = "pi!!";
			break;
		case "salad":
			m_choice = "sa!!";
			break;
		}
		
		m_hasFood = true;
		
		m_xDestination = X_WAIT_POS;
		m_yDestination = Y_WAIT_POS;
		m_isActing = true;
	}
	
	public void doGoToWaitPos() {
		m_xDestination = X_WAIT_POS;
		m_yDestination = Y_WAIT_POS;
		m_isActing = true;
	}

	public void finishedOrder() {
		m_hasFood = false;
	}
	
		public void pause() {
		m_paused = true;
	}
	
	public void resume() {
		m_paused = false;
	}
	
	public int getXPos() { return m_xPos; }
	public int getYPos() { return m_yPos; }
}
