/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.agents;

import java.awt.Graphics2D;

/**
 *
 * @author cmcammarano
 */
public abstract class AgentGUI {
	
	public abstract void updatePosition();
	public abstract void draw(Graphics2D graphics);
	public abstract boolean isPresent();
}
