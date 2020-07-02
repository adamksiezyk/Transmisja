import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

public class ProductEditor extends DefaultCellEditor {

    private List<String> productData;
    int valueToEdit;

    public ProductEditor() {
        super(new JTextField());
    }

    @Override
    public boolean isCellEditable(EventObject anEvent) {
        if (anEvent instanceof MouseEvent) return false;
        else if (anEvent instanceof KeyEvent) return false;
        return super.isCellEditable(anEvent);
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        Component editor =  super.getTableCellEditorComponent(table, value, isSelected, row, column);

        valueToEdit = column;

        // Get client data
        String productName = (String)table.getValueAt(row, 0);
        String productPrice = (String)table.getValueAt(row, 1);
        List<String> productData = new ArrayList<>();
        productData.add(productName);
        productData.add(productPrice);
        this.productData = productData;

        return editor;
    }

    @Override
    public boolean stopCellEditing() {
        // Get new value
        String newValue = (String)getCellEditorValue();
        // Change value in productMap data structure
        NewTransmission.changeProduct(productData, valueToEdit, newValue);
        return super.stopCellEditing();
    }
}