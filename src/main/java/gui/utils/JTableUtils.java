package gui.utils;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.util.Vector;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import controllers.ConstantController;

public class JTableUtils {

    private JTableUtils() {

    }

    public static void preferredColumnWidthByColumnName(JTable jTable, int columnIndex) {
        TableColumn column = jTable.getColumnModel().getColumn(columnIndex);
        TableCellRenderer headerRenderer = jTable.getTableHeader().getDefaultRenderer();
        Component headerComponent = headerRenderer.getTableCellRendererComponent(jTable, column.getHeaderValue(), false, false, -1, columnIndex);
        FontMetrics fontMetrics = headerComponent.getFontMetrics(headerComponent.getFont());

        int minWidth = fontMetrics.stringWidth(column.getHeaderValue().toString());
        int extraWidth = fontMetrics.charWidth('0') * 2;

        minWidth += extraWidth;

        column.setPreferredWidth(minWidth);
    }

    public static void preferredColumnWidthByValues(JTable jTable, int columnIndex) {
        TableColumn column = jTable.getColumnModel().getColumn(columnIndex);
        TableCellRenderer cellRenderer = jTable.getCellRenderer(0, columnIndex);
        Component cellComponent = cellRenderer.getTableCellRendererComponent(jTable, column.getHeaderValue(), false, false, 0, columnIndex);
        FontMetrics fontMetrics = cellComponent.getFontMetrics(cellComponent.getFont());

        int minWidth = 0;

        for (int row = 0; row < jTable.getRowCount(); row++) {
            Object value = jTable.getValueAt(row, columnIndex);
            String text = (value != null) ? value.toString() : "";

            int width = fontMetrics.stringWidth(text);

            minWidth = Math.max(minWidth, width);
        }

        int extraWidth = fontMetrics.charWidth('0') * 2;

        minWidth += extraWidth;

        column.setPreferredWidth(minWidth);
    }

    public static void setNullInRed(JTable table) {
        table.setDefaultRenderer(
            Object.class, (table1, value, isSelected, hasFocus, row, column) -> {
                Component component = new DefaultTableCellRenderer()
                    .getTableCellRendererComponent(table1, value, isSelected, hasFocus, row, column);

                if (value instanceof String text) {
                    if (text.equals(ConstantController.NULL)) {
                        component.setForeground(Color.RED);
                    } else {
                        component.setForeground(Color.BLACK);
                    }
                }

                return component;
            }
        );
    }

    public static void setColumnBold(JTable table, int columnIndex) {
        table
            .getColumnModel()
            .getColumn(columnIndex)
            .setCellRenderer(new DefaultTableCellRenderer() {

                @Override
                public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column
                ) {
                    Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                    component.setFont(new Font(component.getFont().getName(), Font.BOLD, component.getFont().getSize()));

                    return component;
                }
            }
        );
    }

    public static class CustomTableModel extends DefaultTableModel {

        private final boolean[] enabledRows;

        public CustomTableModel(Vector<Vector<Object>> vector1, Vector<String> vector2) {
            super(vector1, vector2);

            this.enabledRows = new boolean[vector1.size()];

            for (int i = 0; i < vector1.size(); i++) {
                this.enabledRows[i] = false;
            }
        }

        @Override
        public boolean isCellEditable(int row, int column) {
			if (row >= this.enabledRows.length) {
				return false;
			}

            return this.enabledRows[row];
        }

        public void setRowEnabled(int row, boolean enabled) {
			if (row < this.enabledRows.length) {
                this.enabledRows[row] = enabled;

                this.fireTableRowsUpdated(row, row);
			}
        }
    }
}
