package model;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

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
	 * Determines if a file is encoded in a Buffered Image
	 *
	 * @param img
	 *            BufferedImage
	 * @return true if a file is encoded or false if a text is encoded
	 */
	public boolean isFileEncoded(BufferedImage img) {
		return Encoder.decodeType(img) == Encoder.FILE_TYPE;
	}

	/**
	 * This method decodes a encoded String in a BufferedImage.
	 *
	 * @param img
	 *            BufferedImage where data is encoded
	 * @return String with the decoded information
	 */
	public String decode(BufferedImage img) {
		return Encoder.decode(img, Encoder.decodeType(img));
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
	public boolean encodeText(BufferedImage img, String text, String path) {

		byte[] data;
		try {
			data = text.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e1) {
			return false;
		}
		Encoder.encode(img, data);

		try {
			File f = new File(path);
			ImageIO.write(img, "png", f);
		} catch (IOException e) {
			return false;
		}
		return true;
	}

	/**
	 * This method encodes a File into a BufferedImage and write it to a
	 * specified path
	 *
	 * @param img
	 *            BufferedImage where data will be encoded
	 * @param file
	 *            File which will be encoded within the image
	 * @param path
	 *            Path to the destination file
	 * @return true if the process was successful; otherwise, false
	 */
	public boolean encodeFile(BufferedImage img, File file, String path) {

		if (!Encoder.encode(img, file))
			return false;

		try {
			File f = new File(path);
			ImageIO.write(img, "png", f);
		} catch (IOException e) {
			return false;
		}
		return true;
	}

	public boolean enoughSizeForText(BufferedImage img, String text) {
		return Encoder.enoughSizeForText(img, text);
	}

	public boolean enoughSizeForFile(BufferedImage img, File file) {
		return Encoder.enoughSizeForFile(img, file);
	}

}
