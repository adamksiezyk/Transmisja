import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.awt.Dimension;
import java.awt.FlowLayout;

public class Product implements ActionListener {

    JFrame frame;
    JPanel panel, panelTop;
    JScrollPane scroll;
    JTable tableClient;
    JTextField textClient, textColor, textSize;
    DefaultTableModel model;
    List<String> productData;

    public Product(MouseEvent e) {

        JTable table = (JTable)e.getSource();
        int row = table.getSelectedRow();
        String name = (String)table.getValueAt(row, 0);
        String price = (String)table.getValueAt(row, 1);
        productData = new ArrayList<String>();
        productData.add(name);
        productData.add(price);

        frame = new JFrame(name);
        frame.setSize(1200, 750);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        frame.add(panel);

        panelTop = new JPanel();
        panelTop.setLayout(new FlowLayout());
        panel.add(panelTop);

        JLabel labelName = new JLabel("Nazwa na FB:");
        panelTop.add(labelName);

        textClient = new JTextField(20);
        textClient.setPreferredSize(new Dimension(100, 30));
        panelTop.add(textClient);

        JLabel labelColor = new JLabel("Kolor:");
        panelTop.add(labelColor);
        
        textColor = new JTextField(20);
        textColor.setPreferredSize(new Dimension(100, 30));
        panelTop.add(textColor);

        JLabel labelSize = new JLabel("Rozmiar:");
        panelTop.add(labelSize);

        textSize = new JTextField(20);
        textSize.setPreferredSize(new Dimension(100, 30));
        panelTop.add(textSize);

        JButton buttonAdd = new JButton("Dodaj");
        buttonAdd.setPreferredSize(new Dimension(100, 30));
        buttonAdd.addActionListener(this);
        buttonAdd.setActionCommand("addClient");
        panelTop.add(buttonAdd);

        tableClient = new JTable();
        String[] columns = {"Nazwa na Facebooku", "Kolor", "Rozmiar"};
        model = new DefaultTableModel();
        model.setColumnIdentifiers(columns);
        tableClient.setModel(model);
        tableClient.setRowHeight(30);
        // Add saved products
        for (String[] client : NewTransmission.getClients(productData)) {
            model.addRow(client);
        }

        // Add popup menu to delete clients
        JPopupMenu menu = new JPopupMenu(){
            // TODO
        };
        JMenuItem deleteClient = new JMenuItem("Usuń klienta");
        deleteClient.addActionListener(this);
        deleteClient.setActionCommand("deleteClient");
        menu.add(deleteClient);
        tableClient.setComponentPopupMenu(menu);
        
        scroll = new JScrollPane(tableClient);
        panel.add(scroll);

        frame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand() == "addClient") {
            String clientName = textClient.getText();
            String clientColor = textColor.getText();
            String clientSize = textSize.getText();
            String[] clientData = new String[]{clientName, clientColor, clientSize};
            model.addRow(new Object[]{clientName, clientColor, clientSize});
            NewTransmission.addClient(productData, clientData);
        }
        else if (e.getActionCommand() == "deleteClient") {
            JTable sourceTable = (JTable)e.getSource();
            int sourceRow = sourceTable.getSelectedRow();
            System.out.println("Delete row: " + sourceRow);
        }
    }
}