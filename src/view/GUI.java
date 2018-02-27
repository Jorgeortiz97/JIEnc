package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.awt.Font;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import model.Controller;

/**
 * 
 * Main panel that contains the whole Graphical User Interface.
 */
public class GUI extends JPanel {
	private static final long serialVersionUID = 1L;

	private static final String WORKDIR = System.getProperty("user.dir");
	private Image backgroundImage;
	private BufferedImage img = null;;
	private Color green = new Color(246, 252, 226);

	public GUI() {
		ImageIcon ii = new ImageIcon("img/background.png");
		backgroundImage = ii.getImage();
		setSize(ii.getIconWidth(), ii.getIconHeight());

		setLayout(null);

		setFocusable(true);
		setPreferredSize(new Dimension(Window.WIDTH, Window.HEIGHT));

		JButton examineBtn = new JButton("Browse");
		examineBtn.setBounds(30, 150, 160, 25);

		JTextField pathText = new JTextField();
		pathText.setBounds(30, 190, 300, 30);
		pathText.setBackground(green);

		JLabel previewLbl = new JLabel("Preview:");
		previewLbl.setBounds(30, 245, 100, 20);

		JLabel imgLbl = new JLabel();
		LineBorder titledBorder = (LineBorder) BorderFactory.createLineBorder(Color.black, 3);
		imgLbl.setBorder(titledBorder);
		imgLbl.setBounds(30, 270, 300, 300);

		JButton encodeBtn = new JButton("Encode");
		encodeBtn.setBounds(375, 60, 160, 40);
		encodeBtn.setEnabled(false);

		JButton decodeBtn = new JButton("Decode");
		decodeBtn.setBounds(610, 60, 160, 40);
		decodeBtn.setEnabled(false);

		JLabel messageLbl = new JLabel("Message:");
		messageLbl.setBounds(375, 115, 100, 20);

		JTextArea msgText = new JTextArea("");
		msgText.setFont(new Font("NewRoman", Font.PLAIN, 14));
		msgText.setEnabled(false);
		msgText.setBackground(green);
		msgText.setForeground(Color.black);
		JScrollPane scrollPane = new JScrollPane(msgText);
		scrollPane.setOpaque(false);
		scrollPane.setBounds(375, 140, 395, 430);
		LineBorder titledBorder2 = (LineBorder) BorderFactory.createLineBorder(Color.black, 3);
		scrollPane.setBorder(titledBorder2);

		examineBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser(WORKDIR);
				FileNameExtensionFilter filter = new FileNameExtensionFilter("PNG", "png");
				fileChooser.setFileFilter(filter);
				int seleccion = fileChooser.showOpenDialog(examineBtn);
				if (seleccion == JFileChooser.APPROVE_OPTION) {
					pathText.setText(fileChooser.getSelectedFile().getPath());

					boolean error = false;
					try {
						img = ImageIO.read(new File(pathText.getText()));
						if (img == null)
							error = true;
					} catch (IOException e1) {
						error = true;
					}
					if (!error) {
						Image dimg = img.getScaledInstance(imgLbl.getWidth(), imgLbl.getHeight(), Image.SCALE_SMOOTH);

						ImageIcon imageIcon = new ImageIcon(dimg);
						imgLbl.setIcon(imageIcon);
					}
					encodeBtn.setEnabled(!error);
					decodeBtn.setEnabled(!error);
					msgText.setEnabled(!error);
				}
			}
		});

		encodeBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				int maxLength = Controller.getInstance().getMaximumLength(img);
				if (maxLength < msgText.getText().length()) {
					JOptionPane
							.showMessageDialog(null,
									"The image could not be saved because the text exceeds the maximum size ("
											+ maxLength + " characters).",
									"Error saving image", JOptionPane.ERROR_MESSAGE);
					return;
				}

				JFileChooser fileChooser = new JFileChooser(WORKDIR);
				FileNameExtensionFilter filter = new FileNameExtensionFilter("PNG", "png");
				fileChooser.setFileFilter(filter);
				int seleccion = fileChooser.showSaveDialog(null);
				if (seleccion == JFileChooser.APPROVE_OPTION) {
					String path = fileChooser.getSelectedFile().getPath();

					if (Controller.getInstance().encodeImage(img, msgText.getText(), path))
						JOptionPane.showMessageDialog(null,
								"Encoded image saved successfully in the following path: " + path);
					else
						JOptionPane.showMessageDialog(null, "The image could not be saved because of an I/O error",
								"Error saving image", JOptionPane.ERROR_MESSAGE);

				}
			}
		});

		decodeBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String decodedStr = Controller.getInstance().decodeImage(img);
				if (decodedStr == null) {
					JOptionPane.showMessageDialog(null, "The image could not be decoded", "Error decoding image",
							JOptionPane.ERROR_MESSAGE);
					decodedStr = "";
				}
				msgText.setText(decodedStr);
			}
		});

		add(examineBtn);
		add(pathText);
		add(previewLbl);
		add(imgLbl);

		add(encodeBtn);
		add(decodeBtn);
		add(messageLbl);
		add(scrollPane);

	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(backgroundImage, 0, 0, null);
	}
}
