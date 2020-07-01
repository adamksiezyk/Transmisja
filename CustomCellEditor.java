import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CustomCellEditor extends DefaultCellEditor {

    private List<String> productData;
    private List<String> clientData;
    int valueToEdit;

    public CustomCellEditor(List<String> productData) {
        super(new JTextField());
        this.productData = productData;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        Component editor =  super.getTableCellEditorComponent(table, value, isSelected, row, column);

        valueToEdit = column;

        // Get client data
        String clientName = (String)table.getValueAt(row, 0);
        String clientColor = (String)table.getValueAt(row, 1);
        String clientSize = (String)table.getValueAt(row, 2);
        List<String> clientData = new ArrayList<>();
        clientData.add(clientName);
        clientData.add(clientColor);
        clientData.add(clientSize);
        this.clientData = clientData;

        return editor;
    }

    @Override
    public boolean stopCellEditing() {
        // Get new value
        String newValue = (String)getCellEditorValue();
        // Change value in productMap data structure
        NewTransmission.changeClient(productData, clientData, valueToEdit, newValue);
        return super.stopCellEditing();
    }
}