/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.agent;

/**
 *
 * @author cmcammarano
 */
public class Message {
	
	private final Object[] m_data;
	
	public Message(Object... data) {
		m_data = data;
	}
	
	public <T> T get(int index) throws IndexOutOfBoundsException {
		if (index < 0 || index >= m_data.length) {
			throw new IndexOutOfBoundsException("Attempted to access a message value that doesn't exist!");
		}
		return (T)m_data[index];
	}
}
