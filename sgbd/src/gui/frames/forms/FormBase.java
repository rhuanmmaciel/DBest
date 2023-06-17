package gui.frames.forms;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public abstract class FormBase extends JDialog {

    protected final JPanel contentPane = new JPanel(new BorderLayout());
    protected final JButton btnCancel = new JButton("Cancelar");
    protected final JButton btnReady = new JButton("Pronto");

    public FormBase (Window window){

        super(window);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                closeWindow();
            }
        });

        initBottomButtons();

    }

    private void initBottomButtons(){

        JPanel bottomPane = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPane.add(btnCancel);
        bottomPane.add(btnReady);
        contentPane.add(bottomPane, BorderLayout.SOUTH);

    }

    protected abstract void closeWindow();

}
