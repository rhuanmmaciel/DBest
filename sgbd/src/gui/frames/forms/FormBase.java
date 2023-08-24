package gui.frames.forms;

import javax.swing.*;
import java.awt.*;

public abstract class FormBase extends JDialog {

    protected final JPanel contentPane = new JPanel(new BorderLayout());
    protected final JButton btnCancel = new JButton("Cancelar");
    protected final JButton btnReady = new JButton("Pronto");

    public FormBase (Window window){

        super(window);
        contentPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        initBottomButtons();

    }

    protected void initBottomButtons(){

        JPanel bottomPane = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPane.add(btnCancel);
        bottomPane.add(btnReady);
        contentPane.add(bottomPane, BorderLayout.SOUTH);
        setContentPane(contentPane);

    }

}
