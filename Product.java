import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
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
    JTextField textClient;
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
        frame.setSize(600, 750);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        frame.add(panel);

        panelTop = new JPanel();
        panelTop.setLayout(new FlowLayout());
        panel.add(panelTop);

        JLabel labelName = new JLabel("Dodaj klienta");
        panelTop.add(labelName);

        textClient = new JTextField(20);
        textClient.setPreferredSize(new Dimension(100, 30));
        panelTop.add(textClient);

        JButton buttonAdd = new JButton("Dodaj");
        buttonAdd.setPreferredSize(new Dimension(100, 30));
        buttonAdd.addActionListener(this);
        buttonAdd.setActionCommand("addClient");
        panelTop.add(buttonAdd);

        tableClient = new JTable();
        String[] columns = {"Nazwa na Facebooku"};
        model = new DefaultTableModel();
        model.setColumnIdentifiers(columns);
        tableClient.setModel(model);
        tableClient.setRowHeight(30);
        // Add saved products
        for (String client : NewTransmission.getClients(productData)) {
            model.addRow(new Object[]{client});
        }
        
        scroll = new JScrollPane(tableClient);
        panel.add(scroll);

        frame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand() == "addClient") {
            String client = textClient.getText();
            model.addRow(new Object[]{client});
            NewTransmission.addClient(productData, client);
        }
    }
}