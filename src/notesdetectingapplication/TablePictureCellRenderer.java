/*************************************************************************
 * This class is a custom cell renderer class for a JTable 
 * Used to add a picture to a cell in the table

 * Category: Utility Classes
 *************************************************************************/


package notesdetectingapplication;


import java.awt.Component;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class TablePictureCellRenderer extends JLabel implements TableCellRenderer {
    private final int KEY_ON = 144;
    private final int KEY_OFF = 128;
    private Icon  On_image = null;
    private Icon Off_image = null;
    
    public TablePictureCellRenderer() {
    super();
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance
                    (notesdetectingapplication.NotesDetectingApplication.class).getContext().getResourceMap(EditNotesDialog.class);
        On_image = resourceMap.getIcon("applyButton.icon");
        Off_image = resourceMap.getIcon("cancelButton.icon");        
    }
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        if((Integer) value == KEY_ON )
        {   
            setText("KEY_ON");
            setIcon(On_image);
        }
        if((Integer) value == KEY_OFF )
        {
            setIcon(Off_image);
            setText("KEY_OFF");
        }
        return this;
    }
}
