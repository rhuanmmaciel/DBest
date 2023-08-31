package gui.frames;

import controller.ConstantController;

import javax.swing.*;

public class ErrorFrame {
	
    public ErrorFrame(String text) {
        JOptionPane.showMessageDialog(null, text, ConstantController.getString("error"), JOptionPane.ERROR_MESSAGE);
    }
    
}

