import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class SearchClient implements ActionListener {

    private JFrame frame;
    private JPanel panel;
    private JScrollPane scrollPane;
    private JTable table;
    private DefaultTableModel model;
    private JPopupMenu menu;
    private JMenuItem delete, edit;
    private String clientName;

    public SearchClient(String name, List<String[]> ordersList) {
        clientName = name;

        // Create frame
        frame = new JFrame(clientName);
        frame.setSize(600, 500);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Create panel
        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        frame.add(panel);

        // Create table for placed orders
        table = new TableWithMenu();
        table.setRowHeight(30);
        table.setDefaultEditor(Object.class, new SearchEditor(clientName));

        String[] columns = {"Produkt", "Kolor", "Rozmiar", "Cena"};
        model = new DefaultTableModel();
        model.setColumnIdentifiers(columns);
        table.setModel(model);

        // Add client's orders to table
        for (String[] order: ordersList) {
            model.addRow(order);
        }

        menu = new JPopupMenu();
        table.setComponentPopupMenu(menu);
        delete = new JMenuItem("Usuń");
        delete.addActionListener(this);
        delete.setActionCommand("delete");
        menu.add(delete);
        edit = new JMenuItem("Edytuj");
        edit.addActionListener(this);
        edit.setActionCommand("edit");
        menu.add(edit);

        scrollPane = new JScrollPane(table);
        panel.add(scrollPane);

        frame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        if (command.equals("delete")) {
            int selectedRow = table.getSelectedRow();
            String productName = (String) table.getValueAt(selectedRow, 0);
            String clientColor = (String) table.getValueAt(selectedRow, 1);
            String clientSize = (String) table.getValueAt(selectedRow, 2);
            String productPrice = (String) table.getValueAt(selectedRow, 3);
            List<String> productData = new ArrayList<>();
            productData.add(productName);
            productData.add(productPrice);
            List<String> clientData = new ArrayList<>();
            clientData.add(clientName);
            clientData.add(clientColor);
            clientData.add(clientSize);

            NewTransmission.removeClient(productData, clientData);
            model.removeRow(selectedRow);
        }
        else if (command.equals("edit")) {
            int selectedRow = table.getSelectedRow();
            int selectedColumn = table.getSelectedColumn();
            if (selectedRow >= 0 && selectedColumn >= 0) {
                table.editCellAt(selectedRow, selectedColumn);
                table.transferFocus();
            }
        }
    }
}
