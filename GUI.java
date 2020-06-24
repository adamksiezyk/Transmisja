import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import java.awt.event.*;
import java.awt.GridLayout;

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