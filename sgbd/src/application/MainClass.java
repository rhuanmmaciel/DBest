package application;

import javax.swing.*;

import controller.MainController;

public class MainClass {
	
	public MainClass() {

		SwingUtilities.invokeLater(() -> {
			MainController main = new MainController();
			main.setVisible(true);
		});

	}

	public static void main(String[] args) {
		
		try {
			
			UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
			
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {

			e.printStackTrace();

		}
		
		new MainClass();
		
	}
	
}
