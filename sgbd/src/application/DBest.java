package application;

import javax.swing.*;

import controllers.MainController;

public class DBest {

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (
			ClassNotFoundException | InstantiationException |
			IllegalAccessException | UnsupportedLookAndFeelException exception
		) {
			exception.printStackTrace();
		}

		SwingUtilities.invokeLater(() -> {
			MainController mainController = new MainController();
			mainController.setVisible(true);
		});
	}
}
