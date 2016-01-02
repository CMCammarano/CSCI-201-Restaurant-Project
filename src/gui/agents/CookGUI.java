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
	
	private int m_xPos;
	private int m_yPos;
	
	private static final int X_SCALE = 20;
	private static final int Y_SCALE = 20;
	
	public CookGUI(Cook cook) {
		m_cook = cook;
		m_xPos = 500;
		m_yPos = 500;
	}

	@Override
	public void updatePosition() {

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
}
