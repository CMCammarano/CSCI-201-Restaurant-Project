/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.agents;

import core.restaurant.agent.Cashier;
import java.awt.Color;
import java.awt.Graphics2D;

/**
 *
 * @author cmcammarano
 */
public class CashierGUI extends AgentGUI {

	private final Cashier m_cashier;
	
	private final int m_xPos;
	private final int m_yPos;
	
	private static final int X_SCALE = 20;
	private static final int Y_SCALE = 20;
	
	public CashierGUI(Cashier cashier) {
		m_cashier = cashier;
		m_xPos = 300;
		m_yPos = 10;
	}
	
	@Override
	public void updatePosition() {

	}

	@Override
	public void draw(Graphics2D graphics) {
		graphics.setColor(Color.CYAN);
		graphics.fillRect(m_xPos, m_yPos, X_SCALE, Y_SCALE);
	}

	@Override
	public boolean isPresent() {
		return true;
	}	
}
