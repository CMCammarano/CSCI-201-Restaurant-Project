/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import core.agent.Agent;
import core.restaurant.Restaurant;
import core.restaurant.agent.Customer;
import core.restaurant.agent.Waiter;
import gui.panels.AnimationPanel;
import gui.panels.RestaurantPanel;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author ColinCammarano
 */
public class RestaurantGUI extends JFrame implements ActionListener {
	
	private static final int COLLUMNS = 2;
	private static final int ROWS = 1;
	private static final int H_EDGE_PADDING = 30;
	private static final int V_EDGE_PADDING = 1;
	private static final int WINDOW_BOUNDS_BUFFER = 100;
	private static final int WINDOW_X = 1280;
	private static final int WINDOW_Y = 720;
	private static final float WINDOW_SCALE_X = 0.4f;
	private static final float WINDOW_SCALE_Y = 0.5f;
	private static final float WINDOW_SCALE_ANIM = 0.6f;
	
	private boolean m_paused = false;
	
	private final JPanel m_mainPanel;
	private final JPanel m_infoPanel;
	private final JLabel m_infoLabel;
	private final JCheckBox m_stateCheckBox;
	private final JButton m_addTableButton;
	private final JButton m_pauseButton;
	
	private Agent m_currentPerson;
	
	private final Restaurant m_restaurant;
	private final AnimationPanel m_animationPanel;
	private final RestaurantPanel m_restaurantPanel;
	
	public RestaurantGUI(Restaurant restaurant) {
		m_restaurant = restaurant;
		m_animationPanel = new AnimationPanel();
		m_restaurantPanel = new RestaurantPanel(m_restaurant, this);
		
		m_mainPanel = new JPanel();
				
		//setLayout(new BoxLayout((Container) getContentPane(), BoxLayout.Y_AXIS)); // Original BoxLayout
		setLayout(new GridBagLayout());
		GridBagConstraints mainConstraint = new GridBagConstraints();
		
		m_mainPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		Dimension restDim = new Dimension((int)(WINDOW_X * WINDOW_SCALE_X), (int) (WINDOW_Y * WINDOW_SCALE_Y));
		m_restaurantPanel.setPreferredSize(restDim);
		m_restaurantPanel.setMinimumSize(restDim);
		m_restaurantPanel.setMaximumSize(restDim);
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		m_mainPanel.add(m_restaurantPanel, c);
		
		// Now, setup the info panel
		Dimension infoDim = new Dimension((int)(WINDOW_X * WINDOW_SCALE_X), (int) (WINDOW_Y * WINDOW_SCALE_Y));
		m_infoPanel = new JPanel();
		m_infoPanel.setPreferredSize(infoDim);
		m_infoPanel.setMinimumSize(infoDim);
		m_infoPanel.setMaximumSize(infoDim);
		m_infoPanel.setBorder(BorderFactory.createTitledBorder("Information"));
		
		m_stateCheckBox = new JCheckBox();
		m_stateCheckBox.setVisible(false);
		m_stateCheckBox.addActionListener(this);
		
		//infoPanel.setLayout(new GridLayout(ROWS, COLLUMNS, H_EDGE_PADDING, V_EDGE_PADDING));
		
		m_infoLabel = new JLabel(); 
		m_infoLabel.setText("<html><pre><i>Click Add to make customers</i></pre></html>");
		m_infoPanel.add(m_infoLabel);
		m_infoPanel.add(m_stateCheckBox);
		
		m_addTableButton = new JButton("Add Table");
		m_addTableButton.setVisible(true);
		m_addTableButton.addActionListener(this);
		
		m_pauseButton = new JButton("Pause");
		m_pauseButton.setVisible(true);
		m_pauseButton.addActionListener(this);
		
		m_infoPanel.add(m_addTableButton);
		//m_infoPanel.add(m_pauseButton);
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 1;
		
		// Animation panel
		Dimension animDim = new Dimension((int)(WINDOW_X * WINDOW_SCALE_ANIM), WINDOW_Y);
		m_animationPanel.setPreferredSize(animDim);
		m_animationPanel.setMinimumSize(animDim);
		m_animationPanel.setMaximumSize(animDim);
		m_animationPanel.setBorder(BorderFactory.createTitledBorder("Animation"));
		
		// Finalizing
		m_mainPanel.add(m_infoPanel, c);
		
		mainConstraint.fill = GridBagConstraints.HORIZONTAL;
		mainConstraint.gridx = 0;
		mainConstraint.gridy = 0;
		add(m_mainPanel, mainConstraint);
		
		mainConstraint.fill = GridBagConstraints.HORIZONTAL;
		mainConstraint.gridx = 1;
		mainConstraint.gridy = 0;
		add(m_animationPanel, mainConstraint);
	}
	
	public void setCustomerEnabled(Customer customer) {
		if (m_currentPerson instanceof Customer) {
			Customer cust = (Customer) m_currentPerson;
			if (customer.equals(cust)) {
				m_stateCheckBox.setEnabled(true);
				m_stateCheckBox.setSelected(false);
			}
		}
	}

	public void updateInfoPanel(Agent person) {
		m_currentPerson = person;

		if (person instanceof Customer) {
			m_stateCheckBox.setVisible(true);
			Customer customer = (Customer) person;
			m_stateCheckBox.setText("Hungry?");
			//Should checkmark be there? 
			m_stateCheckBox.setSelected(customer.getCustomerGUI().isHungry());
			//Is customer hungry? Hack. Should ask customerGui
			m_stateCheckBox.setEnabled(!customer.getCustomerGUI().isHungry());
			m_infoLabel.setText("<html><pre> Name: " + customer.getName() + " </pre></html>");
		}
		
		if (person instanceof Waiter) {
			m_stateCheckBox.setVisible(false);	
			Waiter waiter = (Waiter) person;
			m_infoLabel.setText("<html><pre> Name: " + waiter.getName() + " </pre></html>");
		}
		m_infoPanel.validate();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == m_stateCheckBox) {
			if (m_currentPerson instanceof Customer) {
				Customer c = (Customer) m_currentPerson;
				c.getCustomerGUI().setHungry();
				m_stateCheckBox.setEnabled(false);
			}
		}
		
		if(e.getSource() == m_addTableButton) {
			if(m_animationPanel.getTableCount() >= 6) {
				System.out.println("We can not add any more tables!");
			}
			
			else {
				m_animationPanel.addTable();
				m_restaurantPanel.addTable(150 + (100 * (m_animationPanel.getTableCount() - 1)), 250);
			}
		}
		
		if(e.getSource() == m_pauseButton) {
			m_paused = !m_paused;
			if(m_paused == true) {
				m_restaurantPanel.pause();
				m_pauseButton.setText("Resume");
			}
			
			else {
				m_restaurantPanel.resume();
				m_pauseButton.setText("Pause");
			}
		}
	}
	
	public AnimationPanel getAnimationPanel() { return m_animationPanel; }
}
