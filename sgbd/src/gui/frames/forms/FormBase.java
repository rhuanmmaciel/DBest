package gui.frames.forms;

import controllers.ConstantController;

import javax.swing.*;
import java.awt.*;

public abstract class FormBase extends JDialog {

    protected final JPanel contentPanel;

    protected final JButton btnCancel;

    protected final JButton btnReady;

    protected FormBase(Window window) {
        super(window);

        this.contentPanel = new JPanel(new BorderLayout());
        this.contentPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        this.btnCancel = new JButton(ConstantController.getString("formBase.cancelButton"));
        this.btnReady = new JButton(ConstantController.getString("formBase.readyButton"));

        this.initBottomButtons();
    }

    private void initBottomButtons() {
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(this.btnCancel);
        bottomPanel.add(this.btnReady);

        this.contentPanel.add(bottomPanel, BorderLayout.SOUTH);
        this.setContentPane(this.contentPanel);
    }
}
