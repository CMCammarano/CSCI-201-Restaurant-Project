/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core;

import core.restaurant.Restaurant;
import gui.RestaurantGUI;
import javax.swing.JFrame;

/**
 *
 * @author ColinCammarano
 */
public class Main {
	
	private static final int WINDOW_X = 1280;
	private static final int WINDOW_Y = 720;
	private static final int WINDOW_BOUNDS_X = 64;
	private static final int WINDOW_BOUNDS_Y = 64;
	
	public static void main(String [] args) {
		Restaurant restaurant = new Restaurant();
		RestaurantGUI restaurantGui = new RestaurantGUI(restaurant);
		restaurantGui.setTitle("CSCI-201 Restaurant");
		restaurantGui.setBounds(WINDOW_BOUNDS_X, WINDOW_BOUNDS_Y, WINDOW_X, WINDOW_Y);
		restaurantGui.setVisible(true);
		restaurantGui.setResizable(false);
		restaurantGui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
