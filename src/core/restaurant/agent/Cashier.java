/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.restaurant.agent;

import core.agent.Agent;
import core.agent.Message;

/**
 *
 * @author cmcammarano
 */
public class Cashier extends Agent {

	public Cashier(String name) {
		super(name);
	}	

	@Override
	public boolean update() {
		return false;
	}
	
	public void computeCheck(Message message) {
		
	}
}
