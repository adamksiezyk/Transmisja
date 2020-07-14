import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Product implements ActionListener {

    JFrame frame;
    JPanel panel, panelTop;
    JTable table;
    JScrollPane scroll;
    JTable tableClient;
    JTextField textName, textColor, textSize;
    DefaultTableModel model;
    List<String> productData;
    List<String> blackList = new ArrayList<>();

    public Product(MouseEvent e) {
        table = (JTable) e.getSource();
        int row = table.getSelectedRow();
        String name = (String)table.getValueAt(row, 0);
        String price = (String)table.getValueAt(row, 1);
        productData = new ArrayList<>();
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

        textName = new JTextField(20);
        textName.setPreferredSize(new Dimension(100, 30));
        panelTop.add(textName);

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
        frame.getRootPane().setDefaultButton(buttonAdd);

        tableClient = new TableWithMenu();
        String[] columns = {"Nazwa na Facebooku", "Kolor", "Rozmiar"};
        model = new DefaultTableModel();
        model.setColumnIdentifiers(columns);
        tableClient.setModel(model);
        tableClient.setRowHeight(30);
        tableClient.setDefaultEditor(Object.class, new ClientEditor(productData));

        // Load black list
        try {
            Scanner reader = new Scanner(new File("CzarnaLista.txt"), "UTF-8");
            while (reader.hasNextLine()) {
                blackList.add(reader.nextLine());
            }
        } catch (FileNotFoundException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
        }
        tableClient.setDefaultRenderer(Object.class, new DefaultTableCellRenderer()
        {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
            {
                final Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (blackList.contains(value)) {
                    c.setBackground(Color.RED);
                }
                else {
                    c.setBackground(tableClient.getBackground());
                }
                return c;
            }
        });

        // Add saved clients
        for (List<String> client : NewTransmission.getClients(productData)) {
            model.addRow(client.toArray());
        }

        JPopupMenu menu = new JPopupMenu();
        JMenuItem deleteClient = new JMenuItem("Usuń");
        deleteClient.addActionListener(this);
        deleteClient.setActionCommand("deleteClient");
        JMenuItem editClient = new JMenuItem("Edytuj");
        editClient.addActionListener(this);
        editClient.setActionCommand("editClient");
        JMenuItem blackList = new JMenuItem("Czarna lista");
        blackList.addActionListener(this);
        blackList.setActionCommand("blackList");
        menu.add(deleteClient);
        menu.add(editClient);
        menu.add(blackList);

        tableClient.setComponentPopupMenu(menu);

        scroll = new JScrollPane(tableClient);
        panel.add(scroll);

        frame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if (command.equals("addClient")) {
            String clientName = textName.getText().trim();
            textName.setText("");
            String clientColor = textColor.getText().trim();
            textColor.setText("");
            String clientSize = textSize.getText().trim();
            textSize.setText("");
            List<String> clientData = new ArrayList<>();
            clientData.add(clientName);
            clientData.add(clientColor);
            clientData.add(clientSize);

            model.addRow(clientData.toArray());
            NewTransmission.addClient(productData, clientData);

            textName.requestFocus();
        }
        else if (command.equals("deleteClient")) {
            int selectedRow = tableClient.getSelectedRow();
            String clientName = (String)tableClient.getValueAt(selectedRow, 0);
            String clientColor = (String)tableClient.getValueAt(selectedRow, 1);
            String clientSize = (String)tableClient.getValueAt(selectedRow, 2);
            List<String> clientData = new ArrayList<>();
            clientData.add(clientName);
            clientData.add(clientColor);
            clientData.add(clientSize);

            model.removeRow(selectedRow);
            NewTransmission.removeClient(productData, clientData);

            textName.requestFocus();
        }
        else if (command.equals("editClient")) {
            int selectedRow = tableClient.getSelectedRow();
            int selectedColumn = tableClient.getSelectedColumn();
            if (selectedRow >= 0 && selectedColumn >= 0) {
                tableClient.editCellAt(selectedRow, selectedColumn);
                tableClient.transferFocus();
            }
        }
        else if (command.equals("blackList")) {
            int selectedRow = tableClient.getSelectedRow();
            String name = (String)tableClient.getValueAt(selectedRow, 0);
            blackList.add(name);
            try {
                OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream("CzarnaLista.txt", true), StandardCharsets.UTF_8);
                PrintWriter writer = new PrintWriter(out);
                writer.println(name);
                writer.close();
            } catch (IOException fileNotFoundException) {
                fileNotFoundException.printStackTrace();
            }
            model.fireTableRowsUpdated(selectedRow, selectedRow);
        }
    }
}