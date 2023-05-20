package gui.frames;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import org.kordamp.ikonli.dashicons.Dashicons;
import org.kordamp.ikonli.swing.FontIcon;

import entities.cells.Cell;
import entities.cells.OperationCell;
import gui.frames.main.MainFrame;

@SuppressWarnings("serial")
public class DataFrame extends JDialog implements ActionListener {

	private final int WIDTH = (int)(MainFrame.WIDTH * 1.1);
	private final int HEIGHT = (int)(MainFrame.HEIGHT * 0.6);
	
	private JPanel contentPane;
	private JTable table = new JTable();
	private JButton btnLeft;
	private JButton btnRight;
	private JLabel lblText = new JLabel();
	private JLabel lblPages;
	private List<Map<Integer, Map<String, String>>> pages = new ArrayList<>();
	private Map<Integer, Map<String, String>> data;
	private List<String> columnsName;
	private int numberOfPages;
	private int currentIndex;
	private JButton btnAllLeft;
	private JButton btnAllRight;

	public DataFrame() {

	}

	public DataFrame(Cell cell) {

		super((Window) null, "DataFrame");
		setModal(true);
		
		if(cell instanceof OperationCell operationCell) lblText.setText(operationCell.getType().getDisplayName()+":");
		else lblText.setText(cell.getName()+":");
		
		data = cell.getMapContent();
		columnsName = cell.getColumnsName();
		numberOfPages = (int) Math.ceil((double) data.size() / 15.0);

		for (int i = 0; i < numberOfPages; i++) {

			Map<Integer, Map<String, String>> currentPage = new HashMap<>();

			for (int j = i * 15; j < (i + 1) * 15; j++) {

				currentPage.put(j, data.get(j));

			}

			pages.add(currentPage);

		}

		updateTable(0);
		currentIndex = 0;
		
		initializeGUI();

	}
	
	private void setIcons() {
		
		int buttonsSize = 15;
		
		FontIcon iconLeft = FontIcon.of(Dashicons.CONTROLS_BACK);
		iconLeft.setIconSize(buttonsSize);
		btnLeft.setIcon(iconLeft);
		
		FontIcon iconRight = FontIcon.of(Dashicons.CONTROLS_FORWARD);
		iconRight.setIconSize(buttonsSize);
		btnRight.setIcon(iconRight);
		
		FontIcon iconAllLeft = FontIcon.of(Dashicons.CONTROLS_SKIPBACK);
		iconAllLeft.setIconSize(buttonsSize);
		btnAllLeft.setIcon(iconAllLeft);
		
		FontIcon iconAllRight = FontIcon.of(Dashicons.CONTROLS_SKIPFORWARD);
		iconAllRight.setIconSize(buttonsSize);
		btnAllRight.setIcon(iconAllRight);
		
	}

	private void updateTable(int page) {

		data = pages.size() != 0 ? pages.get(page) : null;

		int firstElement = page * 15;

		DefaultTableModel model = new DefaultTableModel();

		model.addColumn("");

		if (data != null && !data.isEmpty() && data.get(firstElement) != null && !data.get(firstElement).isEmpty()) {

			for (String columnName : data.get(firstElement).keySet()) {
				model.addColumn(columnName);
			}

			int i = page * 15 + 1;
			for (Map<String, String> row : data.values()) {

				if (row != null) {

					Object[] line = new Object[data.get(firstElement).size() + 1];
					line[0] = i++;

					int k = 1;
					for (String key : row.keySet()) {
						line[k] = row.get(key);
						k++;
					}
					model.addRow(line);

				}

			}

		} else {

			model.setColumnIdentifiers(columnsName.toArray());

		}

		table.setModel(model);

		minColumnWidth(table, 0);

		table.getColumnModel().getColumn(0).setResizable(false);
		table.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {
				Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				c.setFont(new Font(c.getFont().getName(), Font.BOLD, c.getFont().getSize()));
				return c;
			}
		});

		table.setDefaultRenderer(Object.class, new TableCellRenderer() {
			private final DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();

			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {

				Component c = renderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

				if (value instanceof String) {
					String texto = (String) value;
					if (texto.equals("null")) {
						c.setForeground(Color.RED);
					} else {
						c.setForeground(Color.BLACK);
					}
				}

				return c;
			}
		});

		table.setEnabled(false);
		table.setFillsViewportHeight(true);
		table.repaint();

	}

	private void minColumnWidth(JTable jTable, int columnIndex) {
		
		int minWidth = 0;
		TableColumn column = jTable.getColumnModel().getColumn(columnIndex);
		TableModel model = jTable.getModel();
		Class<?> columnClass = model.getColumnClass(columnIndex);
		Font font = jTable.getFont();
		FontMetrics fontMetrics = jTable.getFontMetrics(font);
		
		for (int row = 0; row < model.getRowCount(); row++) {
			
			Object value = model.getValueAt(row, columnIndex);
			String text;
			
			if (Number.class.isAssignableFrom(columnClass)) {
				
				Number number = (Number) value;
				text = String.valueOf(number);
				
			} else {

				text = String.valueOf(value);
				
			}
			minWidth = Math.max(minWidth, fontMetrics.stringWidth(text));
			
		}
		
		int extraWidth = fontMetrics.charWidth('0') * 2;
		minWidth += extraWidth;
		column.setMaxWidth(minWidth);

	}

	private void initializeGUI() {

		setBounds(0, 0, WIDTH, HEIGHT);
		setLocationRelativeTo(null);

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);

		JScrollPane scrollPane = new JScrollPane(table);

		btnLeft = new JButton();
		btnLeft.addActionListener(this);

		btnRight = new JButton();
		btnRight.addActionListener(this);

		lblPages = new JLabel();

		btnAllLeft = new JButton();
		btnAllLeft.addActionListener(this);

		btnAllRight = new JButton();
		btnAllRight.addActionListener(this);

		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
		        .addGroup(Alignment.TRAILING,
		                gl_contentPane.createSequentialGroup()
		                        .addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
		                                .addGroup(gl_contentPane.createSequentialGroup()
		                                        .addContainerGap()
		                                        .addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, (int)(WIDTH * 92 / 100), GroupLayout.PREFERRED_SIZE))
		                                .addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
		                                        .addGroup(gl_contentPane.createSequentialGroup()
		                                                .addGap((int)(WIDTH * 1 / 100))
		                                                .addComponent(lblText)
		                                                .addPreferredGap(ComponentPlacement.RELATED, (int)(WIDTH * 85 / 100), Short.MAX_VALUE)
		                                                .addComponent(lblPages))
		                                        .addGroup(gl_contentPane.createSequentialGroup()
		                                                .addContainerGap()
		                                                .addComponent(btnAllLeft)
		                                                .addPreferredGap(ComponentPlacement.RELATED)
		                                                .addComponent(btnLeft)
		                                                .addPreferredGap(ComponentPlacement.UNRELATED)
		                                                .addComponent(btnRight)
		                                                .addPreferredGap(ComponentPlacement.RELATED)
		                                                .addComponent(btnAllRight, GroupLayout.PREFERRED_SIZE, (int)(WIDTH * 4 / 100),
		                                                        GroupLayout.PREFERRED_SIZE))))
		                                .addGap((int)(WIDTH * 5 / 100))));



		gl_contentPane.setVerticalGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
		        .addGroup(gl_contentPane.createSequentialGroup()
		                .addContainerGap()
		                .addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
		                        .addComponent(lblText)
		                        .addComponent(lblPages))
		                .addGap((int)(HEIGHT * 5 / 100))
		                .addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, (int)(HEIGHT * 60 / 100), GroupLayout.PREFERRED_SIZE)
		                .addGap((int)(HEIGHT * 5 / 100))
		                .addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
		                        .addComponent(btnAllRight)
		                        .addComponent(btnRight)
		                        .addComponent(btnLeft)
		                        .addComponent(btnAllLeft))
		                .addContainerGap((int)(HEIGHT * 5 / 100), Short.MAX_VALUE)));


		contentPane.setLayout(gl_contentPane);

		verifyButtons();
		setIcons();
		
		if(table.getRowCount() == 0) lblPages.setText("0/0");

		this.setVisible(true);
	}

	private void verifyButtons() {

		btnLeft.setEnabled(currentIndex != 0);
		btnAllLeft.setEnabled(currentIndex != 0);
		btnRight.setEnabled(currentIndex + 1 < numberOfPages);
		btnAllRight.setEnabled(currentIndex + 1 < numberOfPages);

		lblPages.setText(currentIndex + 1 + "/" + numberOfPages);
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == btnRight) {

			currentIndex++;

		} else if (e.getSource() == btnLeft) {

			currentIndex--;

		} else if (e.getSource() == btnAllRight) {

			currentIndex = numberOfPages - 1;

		} else if (e.getSource() == btnAllLeft) {

			currentIndex = 0;

		}

		updateTable(currentIndex);
		verifyButtons();

	}

}