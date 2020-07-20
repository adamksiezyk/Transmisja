import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.event.*;
import java.awt.GridLayout;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class GUI {

    private static JFrame frame;
    private static JPanel panel;
    private static JButton buttonNew, buttonLoad;

    public GUI() {
        panel = new JPanel();
        panel.setLayout(new GridLayout(2, 1, 20, 100));
        panel.setBorder(new EmptyBorder(80, 80, 150, 80));

        buttonNew = new JButton("Nowa transmisja");
        buttonNew.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new NewTransmission();
                frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
            }
        });
        buttonNew.setActionCommand("newTransmission");
        panel.add(buttonNew);

        buttonLoad = new JButton("Załaduj transmisję");
        buttonLoad.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter("Pliki .txt", "txt");
                fileChooser.setFileFilter(filter);
                int returnValue = fileChooser.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    String path = fileChooser.getSelectedFile().getAbsolutePath();
                    try {
                        NewTransmission loadedTransmission = new NewTransmission();
                        loadedTransmission.setFileName(path);
                        Scanner reader = new Scanner(new File(path), "UTF8");
                        while (reader.hasNextLine()) {
                            String line = reader.nextLine();
                            if (!line.contains("---") && !line.contains("Przesyłka") && !line.contains("Suma")) {
                                String[] orderData = line.split(" - ");

                                List<String> productData = new ArrayList<>();
                                productData.add(orderData[1].trim());
                                String[] price = orderData[4].split(" ");
                                productData.add(price[1].trim());
                                List<String> clientData = new ArrayList<>();
                                clientData.add(orderData[0].trim());
                                clientData.add(orderData[2].trim());
                                clientData.add(orderData[3].trim());
                                loadedTransmission.addClient(productData, clientData);
                            }
                        }
                    } catch (FileNotFoundException fileNotFoundException) {
                        fileNotFoundException.printStackTrace();
                    }

                }
                frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
            }
        });
        panel.add(buttonLoad);

        frame = new JFrame();
        frame.setSize(500, 750);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setTitle("Transmisja");
        frame.setContentPane(panel);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        new GUI();
    }
}