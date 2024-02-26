package gui.frames;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;

public class ProgressFrame extends JDialog {

    private final JProgressBar progressBar = new JProgressBar(0, 100);
    private final JButton btnLoadTuples = new JButton("Carregar tuplas");

    public ProgressFrame(){

        super((Window) null, "Progresso");

        setModal(true);
        setLayout(new FlowLayout());
        progressBar.setStringPainted(true);
        add(progressBar);
        add(btnLoadTuples);

    }

    public void setBtnLoadTuplesListener(ActionListener event){

        btnLoadTuples.addActionListener(e -> btnLoadTuples.setEnabled(false));
        btnLoadTuples.addActionListener(event);

    }

    public void setWindowListener(WindowAdapter event){

        addWindowListener(event);

    }

    public void updateLoadBar(int percentage, String text){

        progressBar.setValue(percentage);
        progressBar.setString(text);

    }

    public void adjust(){

        pack();
        setLocationRelativeTo(null);
        setVisible(true);

    }

}
