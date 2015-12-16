package core.agent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.Semaphore;

/**
 *
 * @author ColinCammarano
 */
public abstract class Agent {
	
	/* PRIVATE MEMBER VARIABLES */
	private AgentThread m_thread;
	private final Semaphore m_stateChanged;
	
	/* PROTECTED MEMBER VARIABLES */
	protected String m_name;
	
	/* CONSTRUCTOR */
	protected Agent() {
		m_name = "Agent";
		m_stateChanged = new Semaphore(1, true);
	}
	
	protected Agent(String name) {
		m_name = name;
		m_stateChanged = new Semaphore(1, true);
	}
	
	/* PROTECTED MEMBER METHODS */
	protected void stateChanged() {
		m_stateChanged.release();
	}
	
	protected void print(String msg) {
		StringBuilder sb = new StringBuilder();
		sb.append(getName());
		sb.append(": ");
		sb.append(msg);
		System.out.println(sb.toString());
	}
	
	/* PUBLIC MEMBER METHODS */
	public abstract boolean update();
	
	public void sendMessage(String message) {
		try {
			Method method = getClass().getDeclaredMethod(message);
			method.invoke(this);
		} catch (NoSuchMethodException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace(System.err);
			//Logger.getLogger(Customer.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	public void sendMessage(String message, Message data) {
		try {
			Method method = getClass().getDeclaredMethod(message, Message.class);
			method.invoke(this, data);
		} catch (NoSuchMethodException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace(System.err);
			//Logger.getLogger(Customer.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	
	public void startThread() {
		if (m_thread == null) {
			m_thread = new AgentThread(getName());
			m_thread.start();
		}
		
		else {
			m_thread.interrupt();
		}
	}
	
	public void stopThread() {
		if (m_thread != null) {
			m_thread.stopAgent();
			m_thread = null;
		}
	}
	
	/* PUBLIC MEMBER FUNCTIONS */
	public String getName() { return m_name; }
	public void setName(String name) { m_name = name; }
	
	/* AGENT THREAD CLASS */
	private class AgentThread extends Thread {
		
		/* PRIVATE MEMBER VARIABLES */
		private volatile boolean m_running;
		
		/* CONSTRUCTOR */
		private AgentThread(String name) {
			super(name);
			m_running = false;
		}
		
		/* PRIVATE MEMBER FUNCTIONS */
		private void stopAgent() {
			m_running = false;
			this.interrupt();
		}
		
		/* PUBLIC MEMBER FUNCTIONS */
		public void run() {
			m_running = true;
			while (m_running) {
				try {
					// The agent blocks until the state changes (typically when animation finishes).
					m_stateChanged.acquire();
					
					// Once the semaphore releases, we update and run our scheduler.
					while(update());
				} catch (InterruptedException e) {
					e.printStackTrace(System.err);
				}
			}
		}
	} 
}
	