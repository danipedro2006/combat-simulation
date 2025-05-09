 

import javax.swing.*;

public class BattleReportWindow extends JFrame {
    private JTextArea reportArea;

    public BattleReportWindow(String title) {
        super(title);

        reportArea = new JTextArea(10, 40);
        reportArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(reportArea);

        add(scrollPane);
        pack();
        setLocationRelativeTo(null); // Center on screen
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    public void updateReport(String text) {
        reportArea.setText(text);
    }

    public void appendReport(String text) {
        reportArea.append(text + "\n");
    }
}
