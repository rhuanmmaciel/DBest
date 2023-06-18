package gui.frames;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import java.awt.BorderLayout;

public class LoadingScreen extends JFrame {

    public LoadingScreen() {

        setTitle("Carregando...");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);

        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.add(progressBar, BorderLayout.CENTER);

        setContentPane(contentPane);
        pack();
        setLocationRelativeTo(null);
    }

    public void showLoadingScreen() {
        setVisible(true);
    }

    public void hideLoadingScreen() {
        setVisible(false);
    }
}
