package gui.frames.forms;

import javax.swing.*;

import java.awt.*;

public abstract class FormBase extends JDialog {

    protected final JPanel contentPanel;

    protected final JButton cancelButton;

    protected final JButton readyButton;

    public FormBase(Window window) {
        super(window);

        this.contentPanel = new JPanel(new BorderLayout());
        this.contentPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        this.cancelButton = new JButton("Cancelar");
        this.readyButton = new JButton("Pronto");

        this.initBottomButtons();
    }

    private void initBottomButtons() {
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(this.cancelButton);
        bottomPanel.add(this.readyButton);

        this.contentPanel.add(bottomPanel, BorderLayout.SOUTH);
        this.setContentPane(this.contentPanel);
    }
}
