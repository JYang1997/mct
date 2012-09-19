package org.acme.example.view;

import gov.nasa.arc.mct.components.ExtendedProperties;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.ResourceBundle;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

@SuppressWarnings("serial")
public class TableSettingsControlPanel extends JPanel {
	private JTable table;
	private MultiColView multiColView;
	private ViewSettings settings; 
	private TaggedComponentManager componentManager;
	/** The resource bundle we should use for getting strings. */
	private static final ResourceBundle bundle = ResourceBundle.getBundle("MultiColResourceBundle"); //NOI18N

	private JCheckBox idBox;
	private JCheckBox titleBox;
	private JCheckBox valueBox;
	private JCheckBox timeBox;

	public TableSettingsControlPanel(ViewSettings settings, JTable table, MultiColView multiColView) {
		this.settings = settings;
		this.table = table;
		this.multiColView = multiColView;
		setLayout(new GridBagLayout());
		setBorder(BorderFactory.createTitledBorder("Columns to Show"));
		componentManager = new TaggedComponentManager();
		idBox = new JCheckBox(bundle.getString("ID"));
		titleBox = new JCheckBox(bundle.getString("TITLE"));
		valueBox = new JCheckBox(bundle.getString("VALUE"));
		timeBox = new JCheckBox(bundle.getString("TIME"));

		updateCheckBoxes();
		addCheckBoxListeners();

		GridBagConstraints ch = new GridBagConstraints();
		ch.fill = GridBagConstraints.HORIZONTAL;
		ch.weightx = 1;
		add(Box.createHorizontalGlue(),ch);
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0;
		c.anchor = GridBagConstraints.FIRST_LINE_START;
		int y = 0;
		c.weighty = 0;
		c.gridx = 0;
		c.gridy = y++;
		add(idBox,c);
		c.gridy = y++;
		add(titleBox,c);
		c.gridy = y++;
		add(valueBox,c);
		c.gridy = y++;
		c.weighty = 1;
		add(timeBox,c);
	}

	private void updateCheckBoxes() {
		if(settings.isDisplayingColumn(ColumnType.ID))       { idBox.setSelected(true); }
		if(settings.isDisplayingColumn(ColumnType.TITLE))    { titleBox.setSelected(true); }
		if(settings.isDisplayingColumn(ColumnType.VALUE))    { valueBox.setSelected(true); }
		if(settings.isDisplayingColumn(ColumnType.TIME))     { timeBox.setSelected(true); }
	}

	private void addCheckBoxListeners() {
		addActionListenerToCheckBox(idBox, ColumnType.ID);
		addActionListenerToCheckBox(titleBox, ColumnType.TITLE);
		addActionListenerToCheckBox(valueBox, ColumnType.VALUE);
		addActionListenerToCheckBox(timeBox, ColumnType.TIME);		
	}

	private void addActionListenerToCheckBox(final JCheckBox checkBox, final ColumnType colType) {
		checkBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(checkBox.isSelected()) { addTableColumn(colType); }
				else { removeTableColumn(colType); }
				saveColumnVisibilityStates();
			}
		});
	}

	//The new column is appended to the right end of the table. 
	public void addTableColumn(ColumnType colType) {
		TableColumn retrievedColumn = settings.retrieveColumn(colType.name());
		TableColumnModel columnModel = table.getColumnModel();  
		columnModel.addColumn(retrievedColumn);
	}
	
	public void removeTableColumn(ColumnType colType) {
		TableColumnModel columnModel = table.getColumnModel();
		int colIndex = columnModel.getColumnIndex(colType.name());		
		TableColumn column = columnModel.getColumn(colIndex);
		settings.hideColumn(column, colType.name());
		columnModel.removeColumn(column);		
	}

	public void updateColumnVisibilityStates(Collection<String> columnIdentifiers) {
		for (String id : columnIdentifiers) {
			if (id.equals(ColumnType.ID.name()))
				idBox.setSelected(false);
			else if (id.equals(ColumnType.TITLE.name())) 
				titleBox.setSelected(false);
			else if (id.equals(ColumnType.VALUE.name())) 
				valueBox.setSelected(false);
			else if (id.equals(ColumnType.TIME.name())) 
				timeBox.setSelected(false);
		}
	}
	
	private void saveColumnVisibilityStates() {		
		ExtendedProperties viewProperties = multiColView.getViewProperties();
		Set<Object> p = viewProperties.getProperty(MultiColView.HIDDEN_COLUMNS_PROP);
		if (p == null) {
			viewProperties.addProperty(MultiColView.HIDDEN_COLUMNS_PROP, "");
			p = viewProperties.getProperty(MultiColView.HIDDEN_COLUMNS_PROP);
		}
		p.clear();
		for (String id : settings.getHiddenColumnIds())
			p.add(id);
		multiColView.getManifestedComponent().save();
	}
}
