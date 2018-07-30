package launcher;

import view.Window;

/**
 *
 * Main Class that launches the application
 *
 * @author Jorge
 * @version 2.0
 */
public class Launcher {
	public static void main(String[] args) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				new Window();
			}
		});
	}
}
