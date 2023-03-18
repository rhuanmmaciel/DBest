package application;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class MainClass {
	public MainClass() {
		ActionClass a = new ActionClass();
		a.setVisible(true);
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
