package gui.frames.forms;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Window;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import controllers.ConstantController;

public abstract class FormBase extends JDialog {

    protected final JPanel contentPanel;

    protected final JButton cancelButton;

    protected final JButton readyButton;

    protected FormBase(Window window) {
        super(window);

        this.contentPanel = new JPanel(new BorderLayout());
        this.contentPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        this.cancelButton = new JButton(ConstantController.getString("formBase.cancelButton"));
        this.readyButton = new JButton(ConstantController.getString("formBase.readyButton"));

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
