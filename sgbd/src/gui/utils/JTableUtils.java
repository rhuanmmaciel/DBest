package gui.utils;

import java.awt.*;
import java.util.Vector;

import javax.swing.JTable;
import javax.swing.table.*;

public class JTableUtils {

	public static void minColumnWidthByColumnName(JTable jTable, int columnIndex) {

		TableColumn column = jTable.getColumnModel().getColumn(columnIndex);
		TableCellRenderer headerRenderer = jTable.getTableHeader().getDefaultRenderer();
		Component headerComponent = headerRenderer.getTableCellRendererComponent(jTable, column.getHeaderValue(), false, false, -1, columnIndex);
		FontMetrics fontMetrics = headerComponent.getFontMetrics(headerComponent.getFont());

		int minWidth = fontMetrics.stringWidth(column.getHeaderValue().toString());
		int extraWidth = fontMetrics.charWidth('0') * 2;
		minWidth += extraWidth;

		column.setMinWidth(minWidth);

	}

	public static void minColumnWidthByValues(JTable jTable, int columnIndex) {
		int minWidth = 0;
		TableColumn column = jTable.getColumnModel().getColumn(columnIndex);
		TableCellRenderer cellRenderer = jTable.getCellRenderer(0, columnIndex);
		Component cellComponent = cellRenderer.getTableCellRendererComponent(jTable, column.getHeaderValue(), false, false, 0, columnIndex);
		FontMetrics fontMetrics = cellComponent.getFontMetrics(cellComponent.getFont());

		for (int row = 0; row < jTable.getRowCount(); row++) {
			Object value = jTable.getValueAt(row, columnIndex);
			String text = (value != null) ? value.toString() : "";
			int width = fontMetrics.stringWidth(text);
			minWidth = Math.max(minWidth, width);
		}

		int extraWidth = fontMetrics.charWidth('0') * 2;
		minWidth += extraWidth;
		column.setMinWidth(minWidth);
	}

	public static void adjustTableColumns(JTable jTable, int tableWidth) {
		int totalWidth = 0;
		TableColumnModel columnModel = jTable.getColumnModel();
		int columnCount = columnModel.getColumnCount();

		for (int i = 0; i < columnCount; i++) {
			TableColumn column = columnModel.getColumn(i);
			totalWidth += column.getPreferredWidth();
		}

		double scaleFactor = (double) tableWidth / totalWidth;

		for (int i = 0; i < columnCount; i++) {
			TableColumn column = columnModel.getColumn(i);
			int preferredWidth = column.getPreferredWidth();
			int newWidth = (int) (preferredWidth * scaleFactor);
			column.setPreferredWidth(newWidth);
		}
	}


	public static void setNullInRed(JTable table) {

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

	}

	@SuppressWarnings("serial")
	public static void setColumnBold(JTable table, int i) {

		table.getColumnModel().getColumn(i).setCellRenderer(new DefaultTableCellRenderer() {
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {
				Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				c.setFont(new Font(c.getFont().getName(), Font.BOLD, c.getFont().getSize()));
				return c;
			}
		});

	}

	@SuppressWarnings("serial")
	public class CustomTableModel extends DefaultTableModel {
	    private boolean[] enabledRows;

	    public CustomTableModel(Vector<Vector<Object>> vector, Vector<String> vector2) {
	        super(vector, vector2);
	        enabledRows = new boolean[vector.size()];
	        for (int i = 0; i < vector.size(); i++) {
	            enabledRows[i] = false;
	        }
	    }

	    @Override
	    public boolean isCellEditable(int row, int column) {
	    	if(row >= enabledRows.length)
	    		return false;
	        return enabledRows[row];
	    }

	    public void setRowEnabled(int row, boolean enabled) {
	    	if(row >= enabledRows.length)
	    		return;
	        enabledRows[row] = enabled;
	        fireTableRowsUpdated(row, row);
	    }
	}

}
