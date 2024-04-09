package gui.frames;

import javax.swing.*;
import java.awt.*;

public class ProgressBarFrame extends JDialog {

    private JProgressBar progressBar;

    public ProgressBarFrame(Frame owner, String title) {
        super(owner, title);
        initComponents();
    }

    private void initComponents() {
        progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);

        setLayout(new BorderLayout());
        add(progressBar, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(getOwner());
    }

    public void showDialog() {
        setVisible(true);
    }

    public void hideDialog() {
        setVisible(false);
    }
}
