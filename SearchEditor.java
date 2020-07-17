import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

public class SearchEditor extends DefaultCellEditor {

    private List<String> productData;
    private List<String> clientData;
    String clientName;
    int valueToEdit;

    public SearchEditor(String clientName) {
        super(new JTextField());
        this.clientName = clientName;
    }

    @Override
    public boolean isCellEditable(EventObject anEvent) {
        if (anEvent instanceof MouseEvent) return false;
        else if (anEvent instanceof KeyEvent) return false;
        return super.isCellEditable(anEvent);
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        Component editor = super.getTableCellEditorComponent(table, value, isSelected, row, column);

        valueToEdit = column;

        // Get product data
        String productName = (String) table.getValueAt(row, 0);
        String productPrice = (String) table.getValueAt(row, 3);
        productData = new ArrayList<>();
        productData.add(productName);
        productData.add(productPrice);

        // Get client data
        String clientColor = (String) table.getValueAt(row, 1);
        String clientSize = (String) table.getValueAt(row, 2);
        clientData = new ArrayList<>();
        clientData.add(clientName);
        clientData.add(clientColor);
        clientData.add(clientSize);

        return editor;
    }

    @Override
    public boolean stopCellEditing() {
        // Get new value
        String newValue = (String) getCellEditorValue();
        // Change value in productMap data structure
        NewTransmission.changeClient(productData, clientData, valueToEdit, newValue.trim());

        return super.stopCellEditing();
    }
}
