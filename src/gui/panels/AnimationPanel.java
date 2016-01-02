/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.panels;

import gui.agents.AgentGUI;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 *
 * @author cmcammarano
 */
public class AnimationPanel extends JPanel implements ActionListener {

	private int m_tableCount = 3;
	
	private final int WINDOWX = 450;
	private final int WINDOWY = 350;
	private final Dimension m_bufferSize;

	private final List<AgentGUI> m_agents;
	
	private static final int ZERO = 0;
	private static final int START_TIME = 20;
	private static final int G2_POS_X = 150;
	private static final int G2_POS_Y = 250;
	private static final int G2_SCALE_X = 50;
	private static final int G2_SCALE_Y = 50;
	
	public AnimationPanel() {
		this.setSize(WINDOWX, WINDOWY);
		this.setVisible(true);
		
		m_bufferSize = this.getSize();
		m_agents = new ArrayList<AgentGUI>();
 
		Timer timer = new Timer(START_TIME, this);
		timer.start();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		repaint();  //Will have paintComponent called
	}

	@Override
	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D)g;

		//Clear the screen by painting a rectangle the size of the frame
		g2.setColor(getBackground());
		g2.fillRect(ZERO, ZERO, this.getWidth(), this.getHeight());//WINDOWX, WINDOWY );

		if(m_tableCount >= 6) {
			m_tableCount = 6;
		}
		
		// Base code for filling in tables. I can make a list of them here, then set their coordinates for the agents when they're drawn.
		for(int cnt = 0; cnt < m_tableCount; cnt++) {
			//Here is the table
			g2.setColor(Color.ORANGE);
			g2.fillRect(G2_POS_X + (100 * cnt), G2_POS_Y, G2_SCALE_X, G2_SCALE_Y);//200 and 250 need to be table params
		}	

		// Grill
		g2.setColor(Color.DARK_GRAY);
		g2.fillRect(700, 620, 30, 40);
		
		g2.setColor(Color.BLACK);
		g2.fillRect(705, 625, 20, 30);
		
		// Refrigerator
		g2.setColor(Color.GRAY);
		g2.fillRect(600, 650, 30, 30);
		
		// Plating Area
		g2.setColor(Color.DARK_GRAY);
		g2.fillRect(500, 620, 30, 40);
	
		// Food Area
		g2.setColor(Color.GRAY);
		g2.fillRect(500, 580, 200, 20);
		
		for(AgentGUI gui : m_agents) {
			if (gui.isPresent()) {
				gui.updatePosition();
			}
		}

		for(AgentGUI gui : m_agents) {
			if (gui.isPresent()) {
				gui.draw(g2);
			}
		}
	}

	public int getTableCount() { return m_tableCount; }
	public void addGui(AgentGUI gui) { m_agents.add(gui); }
	
	public void addTable() {
		m_tableCount++;
		revalidate();
		repaint();
	}
}