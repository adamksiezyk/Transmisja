import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class TableMouseListener extends MouseAdapter {

    private JTable table;

    public TableMouseListener(JTable table) {
        this.table = table;
    }

    @Override
    public void mousePressed(MouseEvent event) {
        // selects the row at which point the mouse is clicked
        Point point = event.getPoint();
        int currentRow = table.rowAtPoint(point);
        table.setRowSelectionInterval(currentRow, currentRow);
    }
}
