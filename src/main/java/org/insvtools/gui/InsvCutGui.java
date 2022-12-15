package org.insvtools.gui;

import javax.swing.*;
import java.awt.*;

// TODO
public class InsvCutGui {
    private static final String BUTTON_CUT_TEXT = "Cut";
    private static final String BUTTON_CLOSE_TEXT = "Close";

    public static void main(String[] args) {
        // Create and set up a frame window
        JFrame frame = new JFrame("INSV Cut");
        frame.getContentPane().setLayout(new BorderLayout());
        //frame.setPreferredSize(new Dimension(640, 480));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Define new buttons
        JButton jbCut = new JButton(BUTTON_CUT_TEXT);
        jbCut.addActionListener(e -> JOptionPane.showMessageDialog(null, "TODO", "Cut the file", JOptionPane.WARNING_MESSAGE));
        JButton jbClose = new JButton(BUTTON_CLOSE_TEXT);
        jbClose.addActionListener(e -> frame.dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        frame.add(buttonPanel, BorderLayout.SOUTH);
        buttonPanel.add(jbCut);
        buttonPanel.add(jbClose);

/*
        String[][] data = new String[][] {};
        String[] cols = new String[] {"Input file", "Output file"};
        JTable jt = new JTable(data, cols);
        frame.add(jt, BorderLayout.CENTER);
*/
/*
        JFileChooser insvFile = new JFileChooser();
        frame.add(insvFile);

        // Or

        FileDialog fd = new FileDialog(frame, "Choose a file", FileDialog.LOAD);
        fd.setFilenameFilter((dir, name) -> name.toLowerCase().endsWith(".insv"));
        fd.setVisible(true);
*/
        frame.pack();
        frame.setVisible(true);
    }
}
