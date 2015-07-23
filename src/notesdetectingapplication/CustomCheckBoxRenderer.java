/*************************************************************************
 * This class is a custom cell renderer for JTables
 * Used to add a check box to a cell in a JTable
 * Category: Utility Classes
 *************************************************************************/

package notesdetectingapplication;

import java.awt.Component;
import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author Acer
 */
public class CustomCheckBoxRenderer extends JCheckBox implements TableCellRenderer{
    boolean isSelected = false;    
    public CustomCheckBoxRenderer() {
        super();
    }
        public Component getTableCellRendererComponent(JTable table, Object value,
        boolean isSelected, boolean hasFocus, int row, int column) {
            boolean marked = (Boolean) value;
            if (isSelected) {
            setForeground(table.getSelectionForeground());
            super.setBackground(table.getSelectionBackground());
        } else {
            setForeground(table.getForeground());
            setBackground(table.getBackground());
        }
        setSelected(marked);
        return this;
    }
}
