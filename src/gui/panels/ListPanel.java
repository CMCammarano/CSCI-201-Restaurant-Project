package gui.panels;


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
	static final int LIST_BUTTON_Y_SCALAR = 2;
	static final int LIST_BUTTON_EDGE_PADDING = 20;
		
	public JScrollPane m_customerButtonPane;
	public JScrollPane m_waiterButtonPane;

	private final JTabbedPane m_creationPane;
	
	private final JPanel m_customerView;
	private final JPanel m_customerPane;
	private final JPanel waiterView;
	private final JPanel m_waiterPane;
	
	private final List<JButton> listCustomer;
	private final List<JButton> listWaiters;
	
	// Added for customer text field
	private final JPanel m_customerInputFields;
	private final JCheckBox m_stateCB;
	private final JTextField m_customerName;
	private final JButton m_addPerson;
	
	// Added for waiter text field
	private final JPanel m_waiterInputFields;
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
		waiterView = new JPanel();
		m_waiterPane = new JPanel();
		
		listCustomer = new ArrayList<JButton>();
		listWaiters = new ArrayList<JButton>();
	
		/*****************************************************************************/
		// Customers
		/*****************************************************************************/
		m_customerName = new JTextField();
		m_addPerson = new JButton("Add");
	
		m_customerPane.setLayout(new GridLayout(ROWS, COLLUMNS));
		
		m_stateCB = new JCheckBox();
		//stateCB.setVisible(false);
		m_stateCB.addActionListener(this);
		m_stateCB.setText("Hungry?");
		
		m_customerInputFields = new JPanel();
		m_customerName.addActionListener(this);
		m_customerInputFields.add(m_customerName);
		
		m_customerInputFields.add(m_stateCB);
		m_customerInputFields.setLayout(new GridLayout(COLLUMNS, ROWS));
		
		m_customerPane.add(m_customerInputFields);
		
		m_addPerson.addActionListener(this);
		m_customerPane.add(m_addPerson);
		
		m_customerView.setLayout(new BoxLayout((Container) m_customerView, BoxLayout.Y_AXIS));
		m_customerButtonPane.setViewportView(m_customerView);
		m_customerPane.add(m_customerButtonPane);
		
		/*****************************************************************************/
		// Waiters
		/*****************************************************************************/
		m_waiterName = new JTextField();
		m_addWaiter = new JButton("Add");
		
		m_waiterPane.setLayout(new GridLayout(ROWS, COLLUMNS));
		
		m_waiterName.addActionListener(this);
		m_waiterInputFields = new JPanel();
		m_waiterInputFields.add(m_waiterName);
		m_waiterInputFields.setLayout(new GridLayout(COLLUMNS, ROWS));
		
		m_waiterPane.add(m_waiterInputFields);
		
		m_addWaiter.addActionListener(this);
		m_waiterPane.add(m_addWaiter);
		
		waiterView.setLayout(new BoxLayout((Container) waiterView, BoxLayout.Y_AXIS));
		m_waiterButtonPane.setViewportView(waiterView);
		m_waiterPane.add(m_waiterButtonPane);
		
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

			validate();
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
			waiterView.add(button);
			m_restaurantPanel.addPerson("Waiters", name);
			m_restaurantPanel.showInfo(m_type, name);//puts hungry button on panel
			validate();
		}
	}
}
