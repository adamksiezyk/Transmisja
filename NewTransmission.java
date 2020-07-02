import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.event.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.awt.Dimension;
import java.awt.FlowLayout;

public class NewTransmission implements ActionListener {

    private JFrame frame;
    private JPanel panel, panelTop;
    private JLabel labelName, labelPrice;
    private JButton buttonAdd;
    private JTextField textName;
    private DefaultTableModel model;
    private JTable table;
    private JScrollPane scroll;
    private JSpinner price;
    private static HashMap<List<String>, List<List<String>>> productsMap = new HashMap<>();
    private List<String> summaryList;

    public NewTransmission() {
        frame = new JFrame("Transmisja");
        frame.setSize(600, 750);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Save products and clients to file when closing
        frame.addWindowListener(new WindowAdapter(){
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosed(e);
                summaryList = new ArrayList<>();
                try {
                    LocalDateTime date = LocalDateTime.now();
                    String dateFormat = date.format(DateTimeFormatter.ofPattern("dd-MM-yyy_HH-mm-ss"));
                    PrintWriter file = new PrintWriter(dateFormat + ".txt");
                    for (Map.Entry<List<String>, List<List<String>>> entry : productsMap.entrySet()) {
                        List<String> product = entry.getKey();
                        List<List<String>> clients = entry.getValue();
                        for (List<String> client : clients) {
                            summaryList.add(client.get(0) + ", " + product.get(0) + ", " + client.get(1) + ", " + client.get(2) + ", " + product.get(1));
                        }
                    }
                    Collections.sort(summaryList);
                    String[] first = summaryList.get(0).split(",");
                    for (String line : summaryList) {
                        if (!first[0].equals(line.split(",")[0])) {
                            file.println("-------------------------------------------------------------------");
                        }
                        file.println(line);
                        first = line.split(",");
                    }
                    file.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });

        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        frame.add(panel);

        panelTop = new JPanel();
        panelTop.setLayout(new FlowLayout());

        labelName = new JLabel("Dodaj produkt:");
        panelTop.add(labelName);

        textName = new JTextField(20);
        textName.setPreferredSize(new Dimension(100, 30));
        panelTop.add(textName);

        labelPrice = new JLabel("Cena:");
        panelTop.add(labelPrice);

        price = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 100000.0, 0.01));
        price.setPreferredSize(new Dimension(100, 30));
        ((JSpinner.DefaultEditor)price.getEditor()).getTextField().addKeyListener( new KeyAdapter() {
            @Override
            public void keyReleased( final KeyEvent e ) {
                if ( e.getKeyCode() == KeyEvent.VK_ENTER ) {
                    buttonAdd.doClick();
                }
            }
        } );
        panelTop.add(price);

        buttonAdd = new JButton("Dodaj");
        buttonAdd.addActionListener(this);
        buttonAdd.setActionCommand("addProduct");
        buttonAdd.setPreferredSize(new Dimension(100, 30));
        panelTop.add(buttonAdd);
        frame.getRootPane().setDefaultButton(buttonAdd);

        panel.add(panelTop);

        String[] columns = { "Nazwa", "Cena" };
        table = new TableWithMenu();
        model = new DefaultTableModel();
        model.setColumnIdentifiers(columns);
        table.setModel(model);
        table.setRowHeight(30);
        table.setDefaultEditor(Object.class, new ProductEditor());
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (e.getClickCount() % 2 == 0 && !e.isConsumed()) {
                    e.consume();
                    new Product(e);
                }
            }
        });

        JPopupMenu menu = new JPopupMenu();
        JMenuItem deleteProduct = new JMenuItem("Usuń");
        deleteProduct.addActionListener(this);
        deleteProduct.setActionCommand("deleteProduct");
        menu.add(deleteProduct);
        JMenuItem editProduct = new JMenuItem("Edytuj");
        editProduct.addActionListener(this);
        editProduct.setActionCommand("editProduct");
        menu.add(editProduct);
        table.setComponentPopupMenu(menu);

        scroll = new JScrollPane(table);
        panel.add(scroll);

        frame.setVisible(true);
    }

    public static boolean addClient(List<String> productData, List<String> client) {
        if (productsMap.containsKey(productData)) {
            return productsMap.get(productData).add(client);
        } else return false;
    }

    public static boolean removeClient(List<String> productData, List<String> clientData) {
        if (productsMap.containsKey(productData)) {
            return productsMap.get(productData).remove(clientData);
        } else return false;
    }

    public static boolean changeClient(List<String> productData, List<String> clientData, int valueToEdit, String newValue) {
        if (productsMap.containsKey(productData) && valueToEdit >= 0) {
            for (List<String> client: productsMap.get(productData)) {
                if (client.equals(clientData)) {
                    client.set(valueToEdit, newValue);
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean changeProduct(List<String> productData, int valueToEdit, String newValue) {
        if (productsMap.containsKey(productData)) {
            List<List<String>> clients = productsMap.remove(productData);
            productData.set(valueToEdit, newValue);
            productsMap.put(productData, clients);
            return true;
        }
        return false;
    }

    public static List<List<String>> getClients(List<String> productData) {
        return productsMap.get(productData);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("addProduct")) {
            String productName = textName.getText();
            textName.setText("");
            Double productPrice = (Double) price.getValue();
            price.setValue(0.0);
            List<String> productData = new ArrayList<>();
            productData.add(productName);
            productData.add(productPrice.toString());

            model.addRow(productData.toArray());
            productsMap.put(productData, new ArrayList<>());

//            int lastRow = table.getRowCount()-1;
//            table.setRowSelectionInterval(lastRow, lastRow);
//            table.requestFocus();
            textName.requestFocus();
        }
        else if (e.getActionCommand().equals("deleteProduct")) {
            int selectedRow = table.getSelectedRow();
            String productName = (String)table.getValueAt(selectedRow, 0);
            String productPrice = (String)table.getValueAt(selectedRow, 1);
            List<String> productData = new ArrayList<>();
            productData.add(productName);
            productData.add(productPrice);

            model.removeRow(selectedRow);
            productsMap.remove(productData);

            textName.requestFocus();
        }
        else if (e.getActionCommand().equals("editProduct")) {
            int selectedRow = table.getSelectedRow();
            int selectedColumn = table.getSelectedColumn();
            if (selectedRow >= 0 && selectedColumn >=0) {
                table.editCellAt(selectedRow, selectedColumn);
                table.transferFocus();
            }
        }
    }
}