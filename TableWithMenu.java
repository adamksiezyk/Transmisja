import java.awt.Point;

import javax.swing.JPopupMenu;
import javax.swing.JTable;

public class TableWithMenu extends JTable {
    
    private static final long serialVersionUID = 1L;

    @Override
    public JPopupMenu getComponentPopupMenu() {
        Point p = getMousePosition();
        int row = rowAtPoint(p);
        setRowSelectionInterval(row, row);
        return super.getComponentPopupMenu();
    }
}