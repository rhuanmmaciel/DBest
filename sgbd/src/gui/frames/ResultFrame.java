package gui.frames;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
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

import entities.Cell;

@SuppressWarnings("serial")
public class ResultFrame extends JDialog implements ActionListener {

	private JPanel contentPane;
	private JTable table = new JTable();
	private JButton btnLeft;
	private JButton btnRight;
	private JLabel lblPages;
	private List<Map<Integer, Map<String, String>>> pages = new ArrayList<>();
	private Map<Integer, Map<String, String>> data;
	private List<String> columnsName;
	private int numberOfPages;
	private int currentIndex;
	private JButton btnAllLeft;
	private JButton btnAllRight;

	public ResultFrame() {
		
	}
	
	public ResultFrame(Cell cell) {

		super((Window) null);
		setModal(true);

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

	private void updateTable(int page) {

		data = pages.size() != 0 ? pages.get(page) : null;

		int firstElement = page * 15;

		DefaultTableModel model = new DefaultTableModel();

		model.addColumn("");

		if (data != null && !data.isEmpty() && data.get(firstElement) != null && !data.get(firstElement).isEmpty()) {

			for (String columnName : data.get(firstElement).keySet()) {
				model.addColumn(columnName);
			}

			int i = 1;
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
		
		table.getColumnModel().getColumn(0).setMaxWidth(30);
		table.getColumnModel().getColumn(0).setResizable(false);
		table.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
	        public Component getTableCellRendererComponent(JTable table, Object value,
	            boolean isSelected, boolean hasFocus, int row, int column) {
	            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	            c.setFont(new Font(c.getFont().getName(), Font.BOLD, c.getFont().getSize()));
	            return c;
	        }
	    });

		table.setDefaultRenderer(Object.class, new TableCellRenderer() {
		    private final DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
		    
		    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
					int row, int column) {
				
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

	private void initializeGUI() {

		setBounds(100, 100, 1400, 450);
		setLocationRelativeTo(null);

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);

		JScrollPane scrollPane = new JScrollPane(table);

		JLabel lblNewLabel = new JLabel("Resultado");

		btnLeft = new JButton("<");
		btnLeft.addActionListener(this);

		btnRight = new JButton(">");
		btnRight.addActionListener(this);

		lblPages = new JLabel();

		btnAllLeft = new JButton("<<");
		btnAllLeft.addActionListener(this);

		btnAllRight = new JButton(">>");
		btnAllRight.addActionListener(this);

		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(gl_contentPane.createParallelGroup(Alignment.LEADING).addGroup(
				Alignment.TRAILING,
				gl_contentPane.createSequentialGroup().addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
						.addGroup(gl_contentPane.createSequentialGroup().addContainerGap().addComponent(scrollPane,
								GroupLayout.PREFERRED_SIZE, 1300, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
								.addGroup(gl_contentPane.createSequentialGroup().addGap(27).addComponent(lblNewLabel)
										.addPreferredGap(ComponentPlacement.RELATED, 1187, Short.MAX_VALUE)
										.addComponent(lblPages))
								.addGroup(gl_contentPane.createSequentialGroup().addContainerGap()
										.addComponent(btnAllLeft).addPreferredGap(ComponentPlacement.RELATED)
										.addComponent(btnLeft).addPreferredGap(ComponentPlacement.UNRELATED)
										.addComponent(btnRight).addPreferredGap(ComponentPlacement.RELATED)
										.addComponent(btnAllRight, GroupLayout.PREFERRED_SIZE, 54,
												GroupLayout.PREFERRED_SIZE))))
						.addGap(63)));
		gl_contentPane.setVerticalGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup().addContainerGap()
						.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE).addComponent(lblNewLabel)
								.addComponent(lblPages))
						.addGap(19)
						.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 272, GroupLayout.PREFERRED_SIZE)
						.addGap(39)
						.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE).addComponent(btnAllRight)
								.addComponent(btnRight).addComponent(btnLeft).addComponent(btnAllLeft))
						.addContainerGap(21, Short.MAX_VALUE)));

		contentPane.setLayout(gl_contentPane);

		verifyButtons();

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