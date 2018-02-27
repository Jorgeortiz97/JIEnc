package model;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

// Singleton Pattern
/**
 * 
 * This class is responsible for responding to the user input and perform
 * interactions on the data model objects (encoder)
 */
public class Controller {

	private static Controller instance;

	public static Controller getInstance() {
		if (instance == null)
			instance = new Controller();
		return instance;
	}

	/**
	 * This method decodes a encoded String in a BufferedImage.
	 * 
	 * @param img
	 *            BufferedImage where data is encoded
	 * @return String with the decoded information
	 */
	public String decodeImage(BufferedImage img) {
		return Encoder.decode(img);
	}

	/**
	 * This method encodes a String into a BufferedImage and write it to a
	 * specified path
	 * 
	 * @param img
	 *            BufferedImage where data will be encoded
	 * @param text
	 *            String which will be encoded within the image
	 * @param path
	 *            Path to the destination file
	 * @return true if the process was successful; otherwise, false
	 */
	public boolean encodeImage(BufferedImage img, String text, String path) {

		Encoder.encode(img, text);

		try {
			File f = new File(path);
			ImageIO.write(img, "png", f);
		} catch (IOException e) {
			return false;
		}
		return true;
	}

	/**
	 * This method returns the maximum amount of bytes that can be encoded in
	 * the image
	 * 
	 * @param img BufferedImage where data will be encoded or is encoded
	 * @return int with the amount of bytes
	 */
	public int getMaximumLength(BufferedImage img) {
		return Integer.min(img.getWidth() * img.getHeight() * 3 / 8 - 3, Encoder.MAX_BYTES);
	}

}
