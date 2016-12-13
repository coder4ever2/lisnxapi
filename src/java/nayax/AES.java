package nayax;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class AES {

	static byte[] keyBytes = new byte[] { 0x00, 0x11, 0x22, 0x33, 0x44, 0x55, 0x66, 0x77, 0x00, 0x11, 0x22, 0x33, 0x44,
			0x55, 0x66, 0x77 };

	public Cipher encryptCipher;

	public Cipher decryptCipher;

	public AES() throws Exception {
		initCiphers();
	}

	/**
	 * Turns array of bytes into string
	 * 
	 * @param buf
	 *            Array of bytes to convert to hex string
	 * @return Generated hex string
	 */
	public static String asHex(byte buf[]) {
		StringBuffer strbuf = new StringBuffer(buf.length * 2);
		int i;

		for (i = 0; i < buf.length; i++) {
			if ((buf[i] & 0xff) < 0x10)
				strbuf.append("0");

			strbuf.append(Long.toString(buf[i] & 0xff, 16));
		}

		return strbuf.toString();
	}

	public static void main(String[] args) throws Exception {

		String message = "This is just an example";

		// Get the KeyGenerator
		AES aes = new AES();

		byte[] encrypted = aes.encrypt("test");
		System.out.println("encrypted string: " + encrypted);

		byte[] original = aes.decrypt(encrypted);
		String originalString = new String(original);
		System.out.println("Original string: " + originalString + " " + asHex(original));

	}

	/**
	 * @param encrypted
	 * @return
	 * @throws BadPaddingException
	 * @throws IllegalBlockSizeException
	 */
	public byte[] decrypt(byte[] encrypted) throws IllegalBlockSizeException, BadPaddingException {
		byte[] original = decryptCipher.doFinal(encrypted);
		return original;
	}

	/**
	 * @param encrypted
	 * @return
	 * @throws BadPaddingException
	 * @throws IllegalBlockSizeException
	 */
	public byte[] decrypt(String encryptedString) throws IllegalBlockSizeException, BadPaddingException {
		byte[] original = decryptCipher.doFinal(encryptedString.getBytes());
		return original;
	}

	/**
	 * @param args
	 * @return
	 * @throws BadPaddingException
	 * @throws IllegalBlockSizeException
	 */
	public byte[] encrypt(String args) throws IllegalBlockSizeException, BadPaddingException {
		byte[] encrypted = encryptCipher.doFinal(args.getBytes());
		return encrypted;
	}

	public void initCiphers() throws Exception {
		KeyGenerator kgen = KeyGenerator.getInstance("AES");
		kgen.init(128); // 192 and 256 bits may not be available

		// Generate the secret key specs.
		SecretKey skey = kgen.generateKey();

		SecretKeySpec skeySpec = new SecretKeySpec(keyBytes, "AES");
		skeySpec.getEncoded();

		// Instantiate the cipher

		encryptCipher = Cipher.getInstance("AES");
		decryptCipher = Cipher.getInstance("AES");

		encryptCipher.init(Cipher.ENCRYPT_MODE, skeySpec);
		decryptCipher.init(Cipher.DECRYPT_MODE, skeySpec);

	}

	public static String byteArrayToHexString(byte[] b) {
		StringBuffer sb = new StringBuffer(b.length * 2);
		for (byte element : b) {
			int v = element & 0xff;
			if (v < 16) {
				sb.append('0');
			}
			sb.append(Integer.toHexString(v));
		}
		return sb.toString().toUpperCase();
	}

	public static byte[] hexStringToByteArray(String s) {
		byte[] b = new byte[s.length() / 2];
		for (int i = 0; i < b.length; i++) {
			int index = i * 2;
			int v = Integer.parseInt(s.substring(index, index + 2), 16);
			b[i] = (byte) v;
		}
		return b;
	}
}
