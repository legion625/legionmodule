package legion.util;

import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AesEncryptionUtil {
	private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
	private static final int IV_LENGTH = 16;

//	private static final String SECRET_KEY = System.getenv("AES_SECRET_KEY");

	public static String encrypt(String aesKey, String plainText) throws Exception {
		
		
		byte[] iv = new byte[IV_LENGTH];
		new SecureRandom().nextBytes(iv);
		IvParameterSpec ivSpec = new IvParameterSpec(iv);

//		SecretKeySpec keySpec = new SecretKeySpec(SECRET_KEY.getBytes(), "AES");
		SecretKeySpec keySpec = new SecretKeySpec(aesKey.getBytes(), "AES");
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);

		byte[] encrypted = cipher.doFinal(plainText.getBytes());
		byte[] combined = new byte[IV_LENGTH + encrypted.length];
		System.arraycopy(iv, 0, combined, 0, IV_LENGTH);
		System.arraycopy(encrypted, 0, combined, IV_LENGTH, encrypted.length);

		return Base64.getEncoder().encodeToString(combined);
	}

	public static String decrypt(String aesKey,String cipherText) throws Exception {
		byte[] combined = Base64.getDecoder().decode(cipherText);
		byte[] iv = new byte[IV_LENGTH];
		byte[] encrypted = new byte[combined.length - IV_LENGTH];
		System.arraycopy(combined, 0, iv, 0, IV_LENGTH);
		System.arraycopy(combined, IV_LENGTH, encrypted, 0, encrypted.length);

		IvParameterSpec ivSpec = new IvParameterSpec(iv);
//		SecretKeySpec keySpec = new SecretKeySpec(SECRET_KEY.getBytes(), "AES");
		SecretKeySpec keySpec = new SecretKeySpec(aesKey.getBytes(), "AES");
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

		return new String(cipher.doFinal(encrypted));
	}
}
