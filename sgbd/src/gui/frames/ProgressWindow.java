package gui.frames;

import javax.swing.*;

public class ProgressWindow extends JFrame {
    private final JProgressBar progressBar;

    public ProgressWindow() {
        setTitle("Progress Window");
        setSize(300, 100);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);

        add(progressBar);
    }

    public void showProgressWindow() {
        setVisible(true);
    }

    public void updateProgress(int progress) {
        progressBar.setValue(progress);
    }

    public void hideProgressWindow() {
        setVisible(false);
    }

}
