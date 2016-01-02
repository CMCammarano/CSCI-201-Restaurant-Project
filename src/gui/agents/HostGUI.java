/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.agents;

import core.restaurant.agent.Host;
import java.awt.Color;
import java.awt.Graphics2D;

/**
 *
 * @author cmcammarano
 */
public class HostGUI extends AgentGUI {

	private final Host m_host;
	
	private final int m_xPos;
	private final int m_yPos;
	
	private static final int X_SCALE = 20;
	private static final int Y_SCALE = 20;
	
	public HostGUI(Host host) {
		m_host = host;
		m_xPos = 50;
		m_yPos = 50;
	}	

	@Override
	public void updatePosition() {

	}

	@Override
	public void draw(Graphics2D graphics) {
		graphics.setColor(Color.PINK);
		graphics.fillRect(m_xPos, m_yPos, X_SCALE, Y_SCALE);
	}

	@Override
	public boolean isPresent() {
		return true;
	}
}
