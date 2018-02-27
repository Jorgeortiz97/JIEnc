package model;

import java.awt.image.BufferedImage;
import java.util.LinkedList;

/**
 * 
 * Class with methods for encoding/decoding information into/from a .PNG image
 */
public class Encoder {

	/**
	 * Maximum amount of bytes to encode in an image
	 */
	public static final int MAX_BYTES = 65536; // 64 kB

	/**
	 * This method encodes 3 bits of information in the chosen pixel of an
	 * image.
	 * 
	 * @param img
	 *            BufferedImage where data will be stored
	 * @param x
	 *            X-Coordinate of the image
	 * @param y
	 *            Y-Coordinate of the image
	 * @param bitA
	 *            first bit of info
	 * @param bitB
	 *            second bit of info
	 * @param bitC
	 *            third bit of info
	 * 
	 */
	private static void setPixel(BufferedImage img, int x, int y, int bitA, int bitB, int bitC) {

		int p = img.getRGB(x, y), a = (p >> 24) & 0xff, r = (p >> 16) & 0xfe | bitA, g = (p >> 8) & 0xfe | bitB,
				b = p & 0xfe | bitC;

		p = (a << 24) | (r << 16) | (g << 8) | b;

		img.setRGB(x, y, p);
	}

	/**
	 * This method decodes a chosen pixel and returns 3 bits of information as
	 * an int array.
	 * 
	 * @param img
	 *            BufferedImage with the encoded info
	 * @param x
	 *            X-Coordinate of the image
	 * @param y
	 *            Y-Coordinate of the image
	 * @return 3 int array with the decoded info as bits
	 */
	private static int[] getPixel(BufferedImage img, int x, int y) {

		int[] bits = new int[3];

		int p = img.getRGB(x, y);
		bits[0] = (p >> 16) & 0x01;
		bits[1] = (p >> 8) & 0x01;
		bits[2] = p & 0x01;

		return bits;
	}

	/**
	 * This method encodes a String into a BufferedImage
	 * 
	 * @param img
	 *            BufferedImage where data will be encoded
	 * @param text
	 *            String which will be encoded within the image
	 */
	public static void encode(BufferedImage img, String text) {

		int pos = 0, width = img.getWidth();

		// Set the message size
		int[] segment = new int[3];
		for (int i = 0; i < 24; i += 3) {
			segment[0] = (text.length() >> (23 - i)) & 0x1;
			segment[1] = (text.length() >> (22 - i)) & 0x1;
			segment[2] = (text.length() >> (21 - i)) & 0x1;
			setPixel(img, pos % width, pos / width, segment[0], segment[1], segment[2]);
			pos++;
		}

		LinkedList<Integer> l = new LinkedList<>();

		// Set the message's content
		for (int seg = 0; seg < text.length() * 8; seg++) {
			l.add((text.charAt(seg / 8) >> (7 - seg % 8)) & 0x01);
			if (l.size() >= 3) {
				setPixel(img, pos % width, pos / width, l.poll(), l.poll(), l.poll());
				pos++;
			}
		}

		// Set remaining bits
		if (l.size() > 0) {
			int[] bits = new int[3];
			Object o;
			for (int i = 0; i < 3; i++) {
				o = l.poll();
				bits[i] = ((o == null) ? 0 : (int) o);
			}
			setPixel(img, pos % width, pos / width, bits[0], bits[1], bits[2]);
			pos++;
		}
	}

	/**
	 * This method decodes a encoded String in a BufferedImage.
	 * 
	 * @param img
	 *            BufferedImage where data is encoded
	 * @return String with the decoded information
	 */
	public static String decode(BufferedImage img) {

		int[] pixel;

		int width = img.getWidth(), pos = 0, length = 0, maxLength = Controller.getInstance().getMaximumLength(img);

		for (int i = 0; i < 24; i += 3) {
			pixel = getPixel(img, pos % width, pos / width);
			length = length | (pixel[0] << (23 - i)) | (pixel[1] << (22 - i)) | (pixel[2] << (21 - i));
			pos++;
		}

		if (length > maxLength)
			return null;

		StringBuffer decStr = new StringBuffer("");
		LinkedList<Integer> l = new LinkedList<>();

		int c;
		for (int i = 0; i < length * 8; i += 3) {
			pixel = getPixel(img, pos % width, pos / width);
			l.add(pixel[0]);
			l.add(pixel[1]);
			l.add(pixel[2]);
			pos++;
			if (l.size() >= 8) {
				c = 0;
				for (int o = 0; o < 8; o++)
					c = c | (l.poll() << (7 - o));
				decStr.append((char) c);
			}
		}

		return decStr.toString();
	}
}
