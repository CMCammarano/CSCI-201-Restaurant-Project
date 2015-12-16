/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.restaurant;

import core.restaurant.agent.Customer;

/**
 *
 * @author cmcammarano
 */
public class Table {
	
	private int m_tableNumber;
	private int m_posX;
	private int m_posY;
	private Customer m_customer;
	
	public Table(int tableNumber, int posX, int posY) {
		m_tableNumber = tableNumber;
		m_posX = posX;
		m_posY = posY;
	}
	
	public Customer getOccupant() { return m_customer; }
	public void setOccupant(Customer customer) { m_customer = customer; }
	public void removeOccupant() { m_customer = null; }
	
	public int getTableNumber() { return m_tableNumber; }
	public void setTableNumber(int tableNumber) { m_tableNumber = tableNumber; }
	
	public int getPosX() { return m_posX; }
	public void setPosX(int posX) { m_posX = posX; }
	
	public int getPosY() { return m_posY; }
	public void setPosY(int posY) { m_posY = posY; }
	
	@Override
	public String toString() { return "Table " + m_tableNumber; }
}
