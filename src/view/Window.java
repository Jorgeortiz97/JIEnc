package view;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 *
 * JFrame that contains the graphical interface
 *
 * @author JorgeOrtiz
 * @version 1.0
 */
public class Window extends JFrame {

	private static final long serialVersionUID = 1L;
	public static final int WIDTH = 800, HEIGHT = 600;

	public Window() {
		setTitle("Java Image Encoder");
		ImageIcon icon = new ImageIcon("img/logo.png");
		setIconImage(icon.getImage());
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(WIDTH, HEIGHT);
		setVisible(true);
		setResizable(false);

		JMenuBar menuBar = new JMenuBar();
		JMenu menu = new JMenu("Help     ");
		JMenuItem infoItem = new JMenuItem("Info"), aboutItem = new JMenuItem("About");

		infoItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null,
						"JIEnc is an application that allows you to encode a message within a PNG image.\n"
								+ "The first step is to browse a PNG image, then you can encode a message inside the image\n"
								+ " or decode an existing encoded image.",
						"Information", JOptionPane.INFORMATION_MESSAGE, icon);
			}
		});

		JButton btnLink = new JButton("Open in browser");
		JPanel btnPanel = new JPanel();
		btnPanel.add(Box.createRigidArea(new Dimension(40, 0)));
		btnPanel.add(btnLink);
		btnPanel.add(Box.createRigidArea(new Dimension(40, 0)));

		JPanel aboutPanel = new JPanel(new BorderLayout());
		aboutPanel.add(
				new JLabel("<html>The author of this program is Jorge Ortiz. <br>"
						+ "You can follow the development of this application in the following link<br>of GitHub:</html>"),
				BorderLayout.NORTH);
		aboutPanel.add(btnPanel, BorderLayout.CENTER);
		aboutPanel.add(new JLabel("<html><br><font size=2>Current version: 1.0 (release 01 - 03 - 2018)</font></html>",
				SwingConstants.CENTER), BorderLayout.SOUTH);

		btnLink.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (Desktop.isDesktopSupported()) {
					try {
						Desktop.getDesktop().browse(new URI("https://github.com/Jorgeortiz97/JIEnc"));
					} catch (IOException | URISyntaxException e1) {
					}
				}
			}
		});

		aboutItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null, aboutPanel, "About", JOptionPane.INFORMATION_MESSAGE, icon);
			}
		});

		menu.add(infoItem);
		menu.add(aboutItem);
		menuBar.add(menu);
		setJMenuBar(menuBar);

		add(new GUI());
		pack();

	}
}
