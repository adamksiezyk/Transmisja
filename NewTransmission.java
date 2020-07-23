import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewTransmission implements ActionListener {

    private JFrame frame;
    private JPanel panelMain, panelTop;
    private static JLabel labelName, labelSearchClient, labelSearchProduct, labelSold;
    private JButton buttonAdd, buttonSave, buttonSearchClient, buttonSearchProduct;
    private JTextField textName, textSearchClient, textSearchProduct;
    private static DefaultTableModel modelProd;
    private JTable tableProd;
    private JScrollPane scrollProd;
    private JSpinner spinnerPrice;
    private static HashMap<List<String>, List<List<String>>> ordersMap = new HashMap<>();
    private List<String> summaryList;
    private String fileName = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyy_HH-mm-ss")) + ".txt";
    private static int sold = 0;

    public NewTransmission() {
        // Create frame
        frame = new JFrame("Transmisja");
        frame.setSize(600, 500);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Save products and clients to file when closing
        frame.addWindowListener(new WindowAdapter(){
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosed(e);
                saveToFile();
            }
        });

        // Main panel - container
        panelMain = new JPanel();
        panelMain.setLayout(new BoxLayout(panelMain, BoxLayout.Y_AXIS));
        frame.add(panelMain);

        // Top panel
        panelTop = new JPanel();
        GroupLayout layoutAdd = new GroupLayout(panelTop);
        panelTop.setLayout(layoutAdd);
        panelMain.add(panelTop);

        // Top panel - components
        labelName = new JLabel("Dodaj produkt:");

        labelSearchClient = new JLabel("Szukaj klienta:");

        labelSearchProduct = new JLabel("Szukaj produkt:");

        textName = new JTextField(20);
        textName.setMaximumSize(new Dimension(500, 30));

        textSearchClient = new JTextField(20);
        textSearchClient.setMaximumSize(new Dimension(500, 30));

        textSearchProduct = new JTextField(20);
        textSearchProduct.setMaximumSize(new Dimension(500, 30));

        spinnerPrice = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 100000.0, 0.01));
        spinnerPrice.setMaximumSize(new Dimension(100, 30));
        ((JSpinner.DefaultEditor) spinnerPrice.getEditor()).getTextField().addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased( final KeyEvent e ) {
                if ( e.getKeyCode() == KeyEvent.VK_ENTER ) {
                    buttonAdd.doClick();
                }
            }
        } );

        buttonAdd = new JButton("Dodaj");
        buttonAdd.addActionListener(this);
        buttonAdd.setActionCommand("addProduct");
        buttonAdd.setPreferredSize(new Dimension(100, 30));
        frame.getRootPane().setDefaultButton(buttonAdd);

        buttonSearchClient = new JButton("Szukaj");
        buttonSearchClient.addActionListener(this);
        buttonSearchClient.setActionCommand("searchClient");
        buttonSearchClient.setPreferredSize(new Dimension(100, 30));

        buttonSearchProduct = new JButton("Szukaj");
        buttonSearchProduct.addActionListener(this);
        buttonSearchProduct.setActionCommand("searchProduct");
        buttonSearchProduct.setPreferredSize(new Dimension(100, 30));

        labelSold = new JLabel("Sprzedano: " + sold);

        buttonSave = new JButton("Zapisz");
        buttonSave.addActionListener(this);
        buttonSave.setActionCommand("save");
        buttonSave.setPreferredSize(new Dimension(100, 30));

        // Top panel - layout
        layoutAdd.setAutoCreateGaps(true);
        layoutAdd.setAutoCreateContainerGaps(true);
        layoutAdd.linkSize(SwingConstants.VERTICAL, labelName, labelSearchClient, labelSearchProduct, labelSold, textName, textSearchClient, textSearchProduct, spinnerPrice, buttonAdd, buttonSearchClient, buttonSearchProduct, buttonSave);
        layoutAdd.linkSize(SwingConstants.HORIZONTAL, buttonAdd, buttonSearchClient, buttonSearchProduct, buttonSave);
        layoutAdd.setHorizontalGroup(
                layoutAdd.createSequentialGroup()
                        .addGroup(layoutAdd.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(labelName)
                                .addComponent(labelSearchClient)
                                .addComponent(labelSearchProduct)
                                .addComponent(labelSold))
                        .addGroup(layoutAdd.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(textName)
                                .addComponent(textSearchClient)
                                .addComponent(textSearchProduct))
                        .addComponent(spinnerPrice)
                        .addGroup(layoutAdd.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(buttonAdd)
                                .addComponent(buttonSearchClient)
                                .addComponent(buttonSearchProduct)
                                .addComponent(buttonSave))
        );
        layoutAdd.setVerticalGroup(
                layoutAdd.createSequentialGroup()
                        .addGroup(layoutAdd.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(labelName)
                                .addComponent(textName)
                                .addComponent(spinnerPrice)
                                .addComponent(buttonAdd))
                        .addGroup(layoutAdd.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(labelSearchClient)
                                .addComponent(textSearchClient)
                                .addComponent(buttonSearchClient))
                        .addGroup(layoutAdd.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(labelSearchProduct)
                                .addComponent(textSearchProduct)
                                .addComponent(buttonSearchProduct))
                        .addGroup(layoutAdd.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(labelSold)
                                .addComponent(buttonSave))
        );

        // Products table
        String[] columnsProd = { "Nazwa", "Cena" };
        tableProd = new TableWithMenu();
        modelProd = new DefaultTableModel();
        modelProd.setColumnIdentifiers(columnsProd);
        tableProd.setModel(modelProd);
        tableProd.setRowHeight(30);
        tableProd.setDefaultEditor(Object.class, new ProductEditor());
        tableProd.setRowSelectionAllowed(true);
        tableProd.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        // Double click mouse listener
        tableProd.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (e.getClickCount() % 2 == 0 && !e.isConsumed()) {
                    e.consume();
                    new Product(tableProd);
                }
            }
        });

        // Table popup menu
        JPopupMenu menu = new JPopupMenu();
        JMenuItem deleteProduct = new JMenuItem("Usuń");
        deleteProduct.addActionListener(this);
        deleteProduct.setActionCommand("deleteProduct");
        menu.add(deleteProduct);
        JMenuItem editProduct = new JMenuItem("Edytuj");
        editProduct.addActionListener(this);
        editProduct.setActionCommand("editProduct");
        menu.add(editProduct);
        tableProd.setComponentPopupMenu(menu);

        scrollProd = new JScrollPane(tableProd);
        panelMain.add(scrollProd);

        frame.setVisible(true);
    }

    // Add client to product
    public static boolean addClient(List<String> productData, List<String> client) {
        if (ordersMap.containsKey(productData)) {
            sold += 1;
            labelSold.setText("Sprzedano: " + sold);

            return ordersMap.get(productData).add(client);
        } else {
            modelProd.addRow(productData.toArray());
            List<List<String>> clientsList = new ArrayList<>();
            clientsList.add(client);
            ordersMap.put(productData, clientsList);

            sold += 1;
            labelSold.setText("Sprzedano: " + sold);

            return true;
        }
    }

    // Remove client from product
    public static boolean removeClient(List<String> productData, List<String> clientData) {
        if (ordersMap.containsKey(productData)) {
            sold -= 1;
            labelSold.setText("Sprzedano: " + sold);

            return ordersMap.get(productData).remove(clientData);
        } else return false;
    }

    // Change client data in given product
    public static boolean changeClient(List<String> productData, List<String> clientData, int valueToEdit, String newValue) {
        if (ordersMap.containsKey(productData) && valueToEdit >= 0) {
            for (List<String> client: ordersMap.get(productData)) {
                if (client.equals(clientData)) {
                    client.set(valueToEdit, newValue);
                    return true;
                }
            }
        }
        return false;
    }

    // Change product data
    public static boolean changeProduct(List<String> productData, int valueToEdit, String newValue) {
        if (ordersMap.containsKey(productData)) {
            List<List<String>> clients = ordersMap.remove(productData);
            productData.set(valueToEdit, newValue);
            ordersMap.put(productData, clients);
            return true;
        }
        return false;
    }

    // Get clients from product
    public static List<List<String>> getClients(List<String> productData) {
        return ordersMap.get(productData);
    }

    // Set file name
    public void setFileName(String name) {
        this.fileName = name;
    }

    // Save orders to file
    public void saveToFile() {
        summaryList = new ArrayList<>();
        double sum = 15;
        try {
            PrintWriter file = new PrintWriter(fileName, "UTF-8");
            for (Map.Entry<List<String>, List<List<String>>> entry : ordersMap.entrySet()) {
                List<String> product = entry.getKey();
                List<List<String>> clients = entry.getValue();
                for (List<String> client : clients) {
                    summaryList.add(client.get(0) + " - " + product.get(0) + " - " + client.get(1) + " - " + client.get(2) + " - Cena: " + product.get(1) + " zł");
                }
            }
            Collections.sort(summaryList);
            if (!summaryList.isEmpty()) {
                String[] first = summaryList.get(0).split(" - ");
                for (String line : summaryList) {
                    if (!first[0].equals(line.split(" - ")[0])) {
                        file.println("Przesyłka: 15 zł");
                        file.println("Suma: " + sum + " zł");
                        sum = 15;
                        file.println("-------------------------------------------------------------------");
                    }
                    file.println(line);
                    first = line.split(" - ");
                    sum += Double.parseDouble(line.split(" - ")[4].split(" ")[1]);
                }
                file.println("Przesyłka: 15 zł");
                file.println("Suma: " + sum + " zł");
                file.println("-------------------------------------------------------------------");
            }
            file.close();
        } catch (IOException e1) {
            e1.printStackTrace();
            JOptionPane.showMessageDialog(new JFrame(), "Błąd podczas zapisywania transmisji.");
        }
    }

    // Button listeners
    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        if (command.equals("addProduct")) {
            String productName = textName.getText().trim();
            textName.setText("");
            Double productPrice = (Double) spinnerPrice.getValue();
            spinnerPrice.setValue(0.0);
            List<String> productData = new ArrayList<>();
            productData.add(productName);
            productData.add(productPrice.toString());

            if (!productName.isEmpty()) {
                modelProd.addRow(productData.toArray());
                ordersMap.put(productData, new ArrayList<>());
            }

            textName.requestFocus();
        }
        else if (command.equals("deleteProduct")) {
            int selectedRow = tableProd.getSelectedRow();
            String productName = (String) tableProd.getValueAt(selectedRow, 0);
            String productPrice = (String) tableProd.getValueAt(selectedRow, 1);
            List<String> productData = new ArrayList<>();
            productData.add(productName);
            productData.add(productPrice);

            modelProd.removeRow(selectedRow);
            sold -= ordersMap.get(productData).size();
            labelSold.setText("Sprzedano: " + sold);
            ordersMap.remove(productData);

            textName.requestFocus();
        }
        else if (command.equals("editProduct")) {
            int selectedRow = tableProd.getSelectedRow();
            int selectedColumn = tableProd.getSelectedColumn();
            if (selectedRow >= 0 && selectedColumn >=0) {
                tableProd.editCellAt(selectedRow, selectedColumn);
                tableProd.transferFocus();
            }
        }
        else if (command.equals("save")) {
            saveToFile();
        }
        else if (command.equals("searchClient")) {
            List<String[]> orders = new ArrayList<>();
            String clientName = textSearchClient.getText().strip();

            // Iterate through products
            String clientNameOrg = "";
            for (Map.Entry<List<String>, List<List<String>>> entry : ordersMap.entrySet()) {
                List<String> product = entry.getKey();
                List<List<String>> clientsList = entry.getValue();

                // Find client's orders
                for (List<String> client: clientsList) {
                    if (clientName.toLowerCase().equals(client.get(0).toLowerCase())) {
                        clientNameOrg = client.get(0);
                        orders.add(new String[] {product.get(0), client.get(1), client.get(2), product.get(1)});
                    }
                }
            }

            // Check if client was found
            if (!orders.isEmpty()) new SearchClient(clientNameOrg, orders);
            else JOptionPane.showMessageDialog(new JFrame(), "Nie znaleziono klienta.");
        }
        else if (command.equals("searchProduct")) {
            String productName = textSearchProduct.getText().trim().toLowerCase();

            ListSelectionModel selectionModel = tableProd.getSelectionModel();
            selectionModel.clearSelection();

            for (int i = 0; i < tableProd.getRowCount(); ++i) {
                String rowName = (String) tableProd.getValueAt(i, 0);

                if (rowName.toLowerCase().contains(productName)) {
                    selectionModel.addSelectionInterval(i,i);
                }
            }
        }
    }
}