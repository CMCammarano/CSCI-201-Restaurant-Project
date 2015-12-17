/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.restaurant;

/**
 *
 * @author cmcammarano
 */
public class Check {
	private float m_bill;
	private String m_choice;
	
	public Check(float bill, String choice) {
		m_bill = bill;
		m_choice = choice;
	}
	
	public float getBill() { return m_bill; }
	public void setBill(float bill) { m_bill = bill; }
	
	public String getChoice() { return m_choice; }
	public void setChoice(String choice) { m_choice = choice; }
}
