import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.text.JTextComponent;

public class TableWithMenu extends JTable {
    
    private static final long serialVersionUID = 1L;

    @Override
    public JPopupMenu getComponentPopupMenu() {
        Point p = getMousePosition();
        int row = rowAtPoint(p);
        int column = columnAtPoint(p);
        setRowSelectionInterval(row, row);
        setColumnSelectionInterval(column, column);
        return super.getComponentPopupMenu();
    }
}