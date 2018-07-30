package model;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;

/**
 *
 * Class with methods for encoding/decoding information into/from a .PNG image
 */
public class Encoder {

	private final static int BUF_SIZE = 8096;
	public final static int TEXT_TYPE = 0;
	public final static int FILE_TYPE = 1;

	/**
	 * This method encodes 3 bits of information in the chosen pixel of an
	 * image.
	 *
	 * @param img
	 *            BufferedImage where data will be stored
	 * @param pos
	 *            Offset in the image
	 * @param bitA
	 *            First bit of information
	 * @param bitB
	 *            Second bit of information
	 * @param bitC
	 *            Third bit of information.
	 */
	private static void setData(BufferedImage img, int pos, int bitA, int bitB, int bitC) {
		int w = img.getWidth();
		int x = pos % w;
		int y = pos / w;

		int p = img.getRGB(x, y), a = (p >> 24) & 0xff, r = (p >> 16) & 0xfe | bitA, g = (p >> 8) & 0xfe | bitB,
				b = p & 0xfe | bitC;

		p = (a << 24) | (r << 16) | (g << 8) | b;

		img.setRGB(x, y, p);
	}

	/**
	 * This method decodes a chosen pixel and returns 3 bits of information as
	 * an integer array.
	 *
	 * @param img
	 *            BufferedImage with the encoded info
	 * @param pos
	 *            offset in the image
	 * @return 3 integer array with the decoded info as bits
	 */
	private static int[] getData(BufferedImage img, int pos) {
		int w = img.getWidth();
		int x = pos % w;
		int y = pos / w;

		int[] bits = new int[3];

		int p = img.getRGB(x, y);
		bits[0] = (p >> 16) & 0x01;
		bits[1] = (p >> 8) & 0x01;
		bits[2] = p & 0x01;

		return bits;
	}

	/**
	 * Encodes an integer in the BufferedImage.
	 *
	 * @param img
	 *            BufferedImage
	 * @param integer
	 *            Integer to encode
	 * @param initialPos
	 *            Position (offset) where the integer will be encoded
	 * @return
	 */
	private static int encodeInt(BufferedImage img, int integer, int initialPos) {

		int pos = initialPos;
		int[] data = new int[3];

		for (int i = 0; i < 24; i += 3) {
			data[0] = (integer >> (23 - i)) & 0x1;
			data[1] = (integer >> (22 - i)) & 0x1;
			data[2] = (integer >> (21 - i)) & 0x1;
			setData(img, pos++, data[0], data[1], data[2]);
		}

		return pos;
	}

	/**
	 * Encodes a byte array with n bytes of length in a BufferedImage.
	 *
	 * @param img
	 *            BufferedImage
	 * @param data
	 *            byte array to be encoded
	 * @param n
	 *            Number of bytes of the byte array to encode
	 * @param initialPos
	 *            offset
	 * @return
	 */
	private static int encodeData(BufferedImage img, byte[] data, int n, int initialPos) {
		int pos = initialPos;
		LinkedList<Integer> l = new LinkedList<>();
		for (int seg = 0; seg < n * 8; seg++) {
			l.add((data[seg / 8] >> (7 - seg % 8)) & 0x01);
			if (l.size() >= 3)
				setData(img, pos++, l.poll(), l.poll(), l.poll());
		}

		// Set remaining bits
		if (l.size() > 0) {
			int[] bits = new int[3];
			for (int i = 0; i < 3; i++) {
				Object o = l.poll();
				bits[i] = ((o == null) ? 0 : (int) o);
			}
			setData(img, pos++, bits[0], bits[1], bits[2]);
		}

		return pos;
	}

	/**
	 * Encodes the PNG header. This header is used to encode a file.
	 *
	 * @param img
	 *            Buffered Image
	 * @param fileName
	 *            Name of the file
	 * @param fileSize
	 *            Size of the file in bytes
	 * @return
	 */
	private static int encodeFileHeader(BufferedImage img, String fileName, long fileSize) {
		int pos = 0;
		// Set type -> "file" (1 byte used).
		setData(img, pos++, 0, 0, FILE_TYPE);

		// Set filename size (3 bytes used).
		byte[] fileNameBytes;
		try {
			fileNameBytes = fileName.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			fileNameBytes = fileName.getBytes();
		}

		pos = encodeInt(img, fileNameBytes.length, pos);
		// Set filename (#filename bytes used).
		pos = encodeData(img, fileNameBytes, fileNameBytes.length, pos);

		// Set filesize (#filesize bytes used).
		return encodeInt(img, (int) fileSize, pos);
	}

	/**
	 * Encodes the PNG header. This header is used to encode a text.
	 *
	 * @param img
	 *            BufferedImage
	 * @param textSize
	 *            Size of the text
	 * @return
	 */
	private static int encodeTextHeader(BufferedImage img, int textSize) {
		int pos = 0;
		// Set type -> "text" (1 byte used).
		setData(img, pos++, 0, 0, TEXT_TYPE);

		return encodeInt(img, textSize, pos);
	}

	/**
	 * This method encodes a file into a BufferedImage
	 *
	 * @param img
	 *            BufferedImage
	 * @param file
	 *            File to be encoded
	 * @return true if the encoding operation was successful
	 */
	public static boolean encode(BufferedImage img, File file) {
		int pos = encodeFileHeader(img, file.getName(), file.length());

		try {
			FileInputStream fis = new FileInputStream(file);

			int n;
			byte[] buf = new byte[BUF_SIZE];
			while ((n = fis.read(buf, 0, BUF_SIZE)) > 0)
				pos = encodeData(img, buf, n, pos);

			fis.close();
		} catch (IOException e) {
			return false;
		}
		return true;
	}

	/**
	 * This method encodes a byte array into a BufferedImage
	 *
	 * @param img
	 *            BufferedImage where data will be encoded
	 * @param text
	 *            Byte array which will be encoded within the image
	 *
	 * @return true
	 */
	public static boolean encode(BufferedImage img, byte[] text) {
		int pos = encodeTextHeader(img, text.length);

		encodeData(img, text, text.length, pos);

		return true;
	}

	/**
	 * Decodes an integer from a BufferedImage.
	 *
	 * @param img
	 *            BufferedImage
	 * @param initialPos
	 *            offset
	 * @return
	 */
	private static int[] decodeInt(BufferedImage img, int initialPos) {
		int pos = initialPos, integer = 0;
		int[] data;

		for (int i = 0; i < 24; i += 3) {
			data = getData(img, pos++);
			integer = integer | (data[0] << (23 - i)) | (data[1] << (22 - i)) | (data[2] << (21 - i));
		}
		return new int[] { pos, integer };
	}

	/**
	 * Decodes some data from a BufferedImage.
	 *
	 * @param img
	 *            Buffered Image
	 * @param initialPos
	 *            offset
	 * @param data
	 *            Buffer where data will be stored
	 * @param n
	 *            bytes to read
	 * @return
	 */
	private static int decodeData(BufferedImage img, int initialPos, byte[] data, int n) {
		StringBuffer decStr = new StringBuffer("");
		LinkedList<Integer> l = new LinkedList<>();

		int[] buf;
		int pos = initialPos, dataPos = 0;
		int b;
		for (int i = 0; i < n * 8; i += 3) {
			buf = getData(img, pos++);
			l.add(buf[0]);
			l.add(buf[1]);
			l.add(buf[2]);
			if (l.size() >= 8) {
				b = 0;
				for (int o = 0; o < 8; o++)
					b = b | (l.poll() << (7 - o));
				data[dataPos++] = (byte) b;
			}
		}

		return pos;
	}

	/**
	 * Decodes the type of a PNG encoded image
	 *
	 * @param img
	 *            BufferedImage
	 * @return Encoder.TEXT_TYPE or Encoder.FILE_TYPE
	 */
	public static int decodeType(BufferedImage img) {
		int[] typeData = getData(img, 0);
		return typeData[2];
	}

	/**
	 * This method decodes a encoded String in a BufferedImage.
	 *
	 * @param img
	 *            BufferedImage where data is encoded
	 * @return String with the decoded information
	 */
	public static String decode(BufferedImage img, int type) {

		int pos = 1;
		int[] value;

		if (type == FILE_TYPE) {
			value = decodeInt(img, pos);
			pos = value[0];
			int fileNameSize = value[1];
			byte[] fileNameBytes = new byte[fileNameSize];
			pos = decodeData(img, pos, fileNameBytes, fileNameSize);
			String fileName = new String(fileNameBytes);
			Integer fileSize = new Integer(0);
			value = decodeInt(img, pos);
			pos = value[0];
			fileSize = value[1];
			try {
				FileOutputStream fos = new FileOutputStream(fileName);
				int n = fileSize, r;
				byte[] buf = new byte[BUF_SIZE];

				while (n > 0) {
					r = Math.min(BUF_SIZE, n);
					pos = decodeData(img, pos, buf, r);
					fos.write(buf, 0, r);
					n -= r;
				}
				fos.close();

			} catch (IOException e) {
				return null;
			}

			return new File(fileName).getAbsolutePath();

		} else {
			Integer textSize = new Integer(0);
			value = decodeInt(img, pos);
			pos = value[0];
			textSize = value[1];
			byte[] buf = new byte[textSize];
			pos = decodeData(img, pos, buf, textSize);

			return new String(buf);
		}
	}

	/**
	 * Determines if BufferedImage is big enough for encoding the desired text.
	 *
	 * @param img
	 *            Buffered Image
	 * @param text
	 *            String to be encoded.
	 * @return true if it is enough.
	 */
	public static boolean enoughSizeForText(BufferedImage img, String text) {
		long totalAvailable = img.getWidth() * img.getHeight() * 3 / 8;

		// Headers: [type][text_size]
		long totalWithoutHeaders = totalAvailable - 1 - 3;

		byte[] data;
		try {
			data = text.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e1) {
			return false;
		}

		return (totalWithoutHeaders > data.length);
	}

	/**
	 * Determines if BufferedImage is big enough for encoding the desired file.
	 *
	 * @param img
	 *            BufferedImage
	 * @param file
	 *            File to be encoded
	 * @return true if it is enough
	 */
	public static boolean enoughSizeForFile(BufferedImage img, File file) {
		long totalAvailable = img.getWidth() * img.getHeight() * 3 / 8;

		byte[] fileName;
		try {
			fileName = file.getName().getBytes("UTF-8");
		} catch (UnsupportedEncodingException e1) {
			return false;
		}

		// Headers: [type][name_size][name][size]
		long totalWithoutHeaders = totalAvailable - 1 - 3 - fileName.length - 3;

		return (totalWithoutHeaders > file.length());
	}
}
