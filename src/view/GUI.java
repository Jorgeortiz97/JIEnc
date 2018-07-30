package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

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
	private File attach = null;

	public GUI() {
		ImageIcon ii = new ImageIcon("img/background.png");
		backgroundImage = ii.getImage();
		setSize(ii.getIconWidth(), ii.getIconHeight());

		setLayout(null);

		setFocusable(true);
		setPreferredSize(new Dimension(Window.WIDTH, Window.HEIGHT));

		JLabel examineLbl = new JLabel("Choose a photo:");
		examineLbl.setBounds(30, 150, 160, 20);

		JButton examineBtn = new JButton("Browse");
		examineBtn.setBounds(170, 150, 160, 25);

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

		JLabel attachLbl = new JLabel("Attach a file:");
		attachLbl.setBounds(375, 130, 150, 20);

		JButton attachBtn = new JButton("Browse");
		attachBtn.setBounds(610, 130, 160, 25);

		JTextField pathAttach = new JTextField();
		pathAttach.setBounds(375, 170, 395, 30);

		attachBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser fileChooser = new JFileChooser(WORKDIR);
				int seleccion = fileChooser.showOpenDialog(examineBtn);
				if (seleccion == JFileChooser.APPROVE_OPTION) {
					attach = fileChooser.getSelectedFile();
					pathAttach.setText(attach.getPath());
				}

			}
		});

		JLabel orLbl = new JLabel("OR");
		orLbl.setBounds(560, 210, 40, 20);

		JLabel messageLbl = new JLabel("Encode a message:");
		messageLbl.setBounds(375, 245, 180, 20);

		JTextArea msgText = new JTextArea("");
		msgText.setFont(new Font("NewRoman", Font.PLAIN, 14));
		msgText.setEnabled(false);
		msgText.setBackground(green);
		msgText.setForeground(Color.black);
		JScrollPane scrollPane = new JScrollPane(msgText);
		scrollPane.setOpaque(false);
		scrollPane.setBounds(375, 270, 395, 300);
		scrollPane.setBorder(titledBorder);

		examineBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser(WORKDIR);
				FileNameExtensionFilter filter = new FileNameExtensionFilter("PNG Images", "png");
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

				boolean txtActive = (!msgText.getText().isEmpty());
				boolean fileActive = (!pathAttach.getText().isEmpty());

				if ((!txtActive && !fileActive) || (txtActive && fileActive)) {
					JOptionPane.showMessageDialog(null,
							"The image could not be created. You must choose only one option to encode: text or file.",
							"Error creating image", JOptionPane.ERROR_MESSAGE);
					return;
				}

				if (txtActive) {
					if (!Controller.getInstance().enoughSizeForText(img, msgText.getText())) {
						JOptionPane.showMessageDialog(null,
								"The image could not be created because it is not big enough to encode the text.",
								"Error creating image", JOptionPane.ERROR_MESSAGE);
						return;
					}
				} else {
					if (!Controller.getInstance().enoughSizeForFile(img, attach)) {
						JOptionPane.showMessageDialog(null,
								"The image could not be created because it is not big enough to encode the file.",
								"Error creating image", JOptionPane.ERROR_MESSAGE);
						return;
					}
				}
				/*
				 *
				 * int maxLength =
				 * Controller.getInstance().getMaximumLength(img); if (maxLength
				 * < msgText.getText().length()) { JOptionPane
				 * .showMessageDialog(null,
				 * "The image could not be saved because the text exceeds the maximum size ("
				 * + maxLength + " characters).", "Error saving image",
				 * JOptionPane.ERROR_MESSAGE); return; }
				 */

				JFileChooser fileChooser = new JFileChooser(WORKDIR);
				FileNameExtensionFilter filter = new FileNameExtensionFilter("PNG image", "png");
				fileChooser.setFileFilter(filter);
				int seleccion = fileChooser.showSaveDialog(null);
				if (seleccion == JFileChooser.APPROVE_OPTION) {
					String path = fileChooser.getSelectedFile().getPath();
					boolean ok;

					if (txtActive)
						ok = Controller.getInstance().encodeText(img, msgText.getText(), path);
					else
						ok = Controller.getInstance().encodeFile(img, attach, path);
					if (ok)
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

				String data = Controller.getInstance().decode(img);

				if (data != null) {
					if (Controller.getInstance().isFileEncoded(img))
						JOptionPane.showMessageDialog(null,
								"The image was decoded. An attach file was generated in the following path: " + data,
								"Image decoded.", JOptionPane.INFORMATION_MESSAGE);
					else {
						JOptionPane.showMessageDialog(null,
								"The image was decoded. You can see the text generated in the text field",
								"Image decoded.", JOptionPane.INFORMATION_MESSAGE);
						msgText.setText(data);
					}
				} else
					JOptionPane.showMessageDialog(null, "The image could not be decoded", "Error decoding image",
							JOptionPane.ERROR_MESSAGE);
			}
		});

		add(examineLbl);
		add(examineBtn);
		add(pathText);
		add(previewLbl);
		add(imgLbl);

		add(encodeBtn);
		add(decodeBtn);

		add(attachLbl);
		add(attachBtn);
		add(pathAttach);
		add(orLbl);
		add(messageLbl);
		add(scrollPane);

	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(backgroundImage, 0, 0, null);
	}
}
