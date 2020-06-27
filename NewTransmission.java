import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableModel;

import java.awt.event.*;
import java.io.IOException;
import java.io.PrintWriter;
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
    private JButton buttonAdd, buttonSave;
    private JTextField textName;
    private DefaultTableModel model;
    private JTable table;
    private JScrollPane scroll;
    private JSpinner price;
    private static HashMap<List<String>, List<String[]>> productsMap = new HashMap<List<String>, List<String[]>>();
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
                summaryList = new ArrayList<String>();
                try {
                    PrintWriter file = new PrintWriter(java.time.LocalDateTime.now() + ".txt");
                    for (Map.Entry<List<String>, List<String[]>> entry : productsMap.entrySet()) {
                        List<String> product = entry.getKey();
                        List<String[]> clients = entry.getValue();
                        for (String[] client : clients) {
                            summaryList.add(client[0] + " " + product.get(0) + " " + client[1] + " " + client[2] + " " + product.get(1));
                        }
                    }
                    Collections.sort(summaryList);
                    String[] first = summaryList.get(0).split(" ");
                    for (String line : summaryList) {
                        if (!(first[0].equals(line.split(" ")[0]) && first[1].equals(line.split(" ")[1]))) {
                            file.println("-------------------------------------------------------------------");
                        }
                        file.println(line);
                        first = line.split(" ");
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
        panelTop.add(price);

        buttonAdd = new JButton("Dodaj");
        buttonAdd.addActionListener(this);
        buttonAdd.setActionCommand("addProduct");
        buttonAdd.setPreferredSize(new Dimension(100, 30));
        panelTop.add(buttonAdd);

        panel.add(panelTop);

        String[] columns = { "Nazwa", "Cena" };
        table = new JTable() {
            private static final long serialVersionUID = 1L;

            public boolean editCellAt(int row, int column, java.util.EventObject e) {
                return false;
            }
        };
        model = new DefaultTableModel();
        model.setColumnIdentifiers(columns);
        table.setModel(model);
        table.setRowHeight(30);
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (e.getClickCount() == 2 && !e.isConsumed()) {
                    e.consume();
                    new Product(e);
                }
            }
        });

        scroll = new JScrollPane(table);
        panel.add(scroll);

        frame.setVisible(true);
    }

    public static boolean addClient(List<String> productData, String[] client) {
        if (productsMap.containsKey(productData)) {
            productsMap.get(productData).add(client);
            return true;
        } else {
            return false;
        }
    }

    public static List<String[]> getClients(List<String> productData) {
        return productsMap.get(productData);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand() == "addProduct") {
            String productName = textName.getText();
            Double productPrice = (Double) price.getValue();
            model.addRow(new Object[] { productName, productPrice.toString() });
            List<String> productData = new ArrayList<String>();
            productData.add(productName);
            productData.add(productPrice.toString());
            productsMap.put(productData, new ArrayList<String[]>());
        }
    }
}