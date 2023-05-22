package gui.frames;

import java.awt.Window;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

@SuppressWarnings("serial")
public class ErrorFrame extends JDialog {

    public ErrorFrame(String error) {
        super((Window) null);
        setModal(true);

        JOptionPane.showMessageDialog(this, error, "Erro", JOptionPane.ERROR_MESSAGE);
    }
}
