package gui.panels;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.ArrayList;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

/**
 * Subpanel of restaurantPanel.
 * This holds the scroll panes for the customers and, later, for waiters
 */

public class ListPanel extends JPanel implements ActionListener {

	// Static final declarations
	static final int COLLUMNS = 1;
	static final int ROWS = 0; // The grid layout interprets this as "as many as I damn well please."
	static final int LIST_BUTTON_Y_SCALAR = 10;
	static final int LIST_BUTTON_EDGE_PADDING = 20;
		
	public JScrollPane m_customerButtonPane;
	public JScrollPane m_waiterButtonPane;

	private final JTabbedPane m_creationPane;
	
	private final JPanel m_customerView;
	private final JPanel m_customerPane;
	private final JPanel m_waiterView;
	private final JPanel m_waiterPane;
	
	private final List<JButton> listCustomer;
	private final List<JButton> listWaiters;
	
	// Added for customer text field
	private final JCheckBox m_stateCB;
	private final JTextField m_customerName;
	private final JButton m_addPerson;
	
	// Added for waiter text field
	private final JTextField m_waiterName;
	private final JButton m_addWaiter;
	
	private final RestaurantPanel m_restaurantPanel;
	private String m_type;

	/**
	 * Constructor for ListPanel.  Sets up all the gui
	 *
	 * @param rp   reference to the restaurant panel
	 * @param type indicates if this is for customers or waiters
	 */
	public ListPanel(RestaurantPanel rp, String type) {
		m_restaurantPanel = rp;
		m_type = type;

		m_customerButtonPane = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		m_waiterButtonPane = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		m_creationPane = new JTabbedPane();
		m_customerView = new JPanel();
		m_customerPane = new JPanel();
		m_waiterView = new JPanel();
		m_waiterPane = new JPanel();
		
		listCustomer = new ArrayList<JButton>();
		listWaiters = new ArrayList<JButton>();
	
		/*****************************************************************************/
		// Customers
		/*****************************************************************************/
		m_customerName = new JTextField();
		m_addPerson = new JButton("Add");
	
		m_customerPane.setLayout(new BorderLayout());
		
		m_stateCB = new JCheckBox();
		m_stateCB.addActionListener(this);
		m_stateCB.setText("Hungry?");
		
		JPanel customerInputFields = new JPanel();
		m_customerName.addActionListener(this);
		
		customerInputFields.setLayout(new BorderLayout());
		customerInputFields.add(m_customerName, BorderLayout.CENTER);
		customerInputFields.add(m_stateCB, BorderLayout.EAST);
		customerInputFields.add(m_addPerson, BorderLayout.SOUTH);
		
		m_customerPane.add(customerInputFields, BorderLayout.NORTH);
		
		m_addPerson.addActionListener(this);
		
		m_customerView.setLayout(new BoxLayout((Container) m_customerView, BoxLayout.Y_AXIS));
		m_customerButtonPane.setViewportView(m_customerView);
		m_customerPane.add(m_customerButtonPane, BorderLayout.CENTER);
		
		/*****************************************************************************/
		// Waiters
		/*****************************************************************************/
		m_waiterName = new JTextField();
		m_addWaiter = new JButton("Add");
		m_addWaiter.addActionListener(this);
		
		JPanel waiterInputFields = new JPanel();
		waiterInputFields.setLayout(new BorderLayout());
		waiterInputFields.add(m_waiterName, BorderLayout.CENTER);
		waiterInputFields.add(m_addWaiter, BorderLayout.SOUTH);
		
		m_waiterPane.setLayout(new BorderLayout());
		
		m_waiterView.setLayout(new BoxLayout((Container) m_waiterView, BoxLayout.Y_AXIS));
		m_waiterButtonPane.setViewportView(m_waiterView);
		m_waiterPane.add(m_waiterButtonPane, BorderLayout.CENTER);
		m_waiterPane.add(waiterInputFields, BorderLayout.NORTH);
		
		/*****************************************************************************/
		// Tabbed Pane
		/*****************************************************************************/
		Dimension dim = new Dimension(230, 360);
		m_creationPane.addTab("Customers", m_customerPane);
		m_creationPane.addTab("Waiters", m_waiterPane);
		m_creationPane.setPreferredSize(dim);
		m_creationPane.setMinimumSize(dim);
		m_creationPane.setMaximumSize(dim);
		add(m_creationPane);
		
		revalidate();
		repaint();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == m_addPerson) {
			addCustomer(m_customerName.getText());
		}
		else if (e.getSource() == m_addWaiter) {
			addWaiter(m_waiterName.getText());
		}
		
		else {
			for (JButton temp : listCustomer){
				if (e.getSource() == temp) {
					m_type = "Customers";
					m_restaurantPanel.showInfo(m_type, temp.getText());
				}
			}
			
			for (JButton temp : listWaiters){
				if (e.getSource() == temp) {
					m_type = "Waiters";
					m_restaurantPanel.showInfo(m_type, temp.getText());
				}
			}
		}
	}

	/**
	 * If the add button is pressed, this function creates
	 * a spot for it in the scroll customerButtonPane, and tells the restaurant panel
	 * to add a new person.
	 *
	 * @param name name of new person
	 */
	public void addCustomer(String name) {
		if (name != null) {
			JButton button = new JButton(name);
			button.setBackground(Color.white);
			Dimension paneSize = m_customerButtonPane.getSize();
			Dimension buttonSize = new Dimension(paneSize.width - LIST_BUTTON_EDGE_PADDING, (int) (paneSize.height / LIST_BUTTON_Y_SCALAR));
			button.setPreferredSize(buttonSize);
			button.setMinimumSize(buttonSize);
			button.setMaximumSize(buttonSize);
			button.addActionListener(this);
			listCustomer.add(button);
			m_customerView.add(button);
			m_restaurantPanel.addPerson(m_type, name);//puts customer on listCustomer
			m_restaurantPanel.showInfo(m_type, name);//puts hungry button on panel
			
			if(m_stateCB.isSelected()) {
				m_restaurantPanel.getLastCustomer().getCustomerGUI().setHungry();
				m_stateCB.setSelected(false);
			}

			revalidate();
			repaint();
		}
	}
	
	public void addWaiter(String name) {
		if (name != null) {
			JButton button = new JButton(name);
			button.setBackground(Color.white);
			Dimension paneSize = m_waiterButtonPane.getSize();
			Dimension buttonSize = new Dimension(paneSize.width - LIST_BUTTON_EDGE_PADDING, (int) (paneSize.height / LIST_BUTTON_Y_SCALAR));
			button.setPreferredSize(buttonSize);
			button.setMinimumSize(buttonSize);
			button.setMaximumSize(buttonSize);
			button.addActionListener(this);
			listWaiters.add(button);
			m_waiterView.add(button);
			m_restaurantPanel.addPerson("Waiters", name);
			m_restaurantPanel.showInfo(m_type, name);//puts hungry button on panel
			revalidate();
			repaint();
		}
	}
}
