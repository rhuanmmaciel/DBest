package gui.frames;

import controllers.ConstantController;

import javax.swing.JOptionPane;

public class ErrorFrame {
	
    public ErrorFrame(String text) {
        JOptionPane.showMessageDialog(null, text, ConstantController.getString("error"), JOptionPane.ERROR_MESSAGE);
    }
    
}

