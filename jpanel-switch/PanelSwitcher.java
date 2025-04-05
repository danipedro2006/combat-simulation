import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PanelSwitcher extends JFrame {
    private JPanel mainPanel;
    private JPanel panel1, panel2;
    private CardLayout cardLayout;

    public PanelSwitcher() {
        setTitle("Panel Switcher");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Panel 1 with a button
        panel1 = new JPanel();
        JButton goToPanel2 = new JButton("Go to Panel 2");
        panel1.add(goToPanel2);

        // Panel 2 with a button
        panel2 = new JPanel();
        JButton goToPanel1 = new JButton("Back to Panel 1");
        panel2.add(goToPanel1);

        // Add panels to mainPanel
        mainPanel.add(panel1, "Panel1");
        mainPanel.add(panel2, "Panel2");

        // Add action listeners
        goToPanel2.addActionListener(e -> cardLayout.show(mainPanel, "Panel2"));
        goToPanel1.addActionListener(e -> cardLayout.show(mainPanel, "Panel1"));

        add(mainPanel);
        setVisible(true);
    }

    public static void main(String[] args) {
        new PanelSwitcher();
    }
}
