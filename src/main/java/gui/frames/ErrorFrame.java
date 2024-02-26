package gui.frames;

import javax.swing.JOptionPane;

public class ErrorFrame {
	
    public ErrorFrame(String text) {
        JOptionPane.showMessageDialog(null, text, "Erro", JOptionPane.ERROR_MESSAGE);
    }
    
}

