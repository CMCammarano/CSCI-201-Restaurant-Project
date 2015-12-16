/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import core.restaurant.Restaurant;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;

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
	private static final float WINDOW_SCALE_X = 0.4f;
	private static final float WINDOW_SCALE_Y = 0.5f;
	private static final float WINDOW_SCALE_ANIM = 0.6f;
	
	private final Restaurant m_restaurant;
	
	public RestaurantGUI(Restaurant restaurant) {
		m_restaurant = restaurant;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
}
