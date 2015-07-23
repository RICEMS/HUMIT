/*************************************************************************
 * This class is a custom cell renderer for JTables
 * Used to add a combo-box to a cell in a JTable
 * Category: Utility Classes
 *************************************************************************/
package notesdetectingapplication;

import java.awt.Component;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author Acer
 */
public class CustomComboBoxRenderer extends JComboBox implements TableCellRenderer{
        public CustomComboBoxRenderer(String[] items) {
        super(items);
    }
        public Component getTableCellRendererComponent(JTable table, Object value,
        boolean isSelected, boolean hasFocus, int row, int column) {
        if (isSelected) {
            setForeground(table.getSelectionForeground());
            super.setBackground(table.getSelectionBackground());
        } else {
            setForeground(table.getForeground());
            setBackground(table.getBackground());
        }

        // Select the current value
        setSelectedItem(value);
        return this;
    }
}
