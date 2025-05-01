package legion.util;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class StrHasher {
	private static final int SALT_LENGTH = 16; // bytes
	private static final int ITERATIONS = 65536;
	private static final int KEY_LENGTH = 256;

	public static String hashPassword(String password) throws Exception {
		byte[] salt = new byte[SALT_LENGTH];
		new SecureRandom().nextBytes(salt);

		byte[] hash = pbkdf2(password.toCharArray(), salt);
		return Base64.getEncoder().encodeToString(salt) + ":" + Base64.getEncoder().encodeToString(hash);
	}

	public static boolean verifyPassword(String password, String stored) throws Exception {
		String[] parts = stored.split(":");
		byte[] salt = Base64.getDecoder().decode(parts[0]);
		byte[] expectedHash = Base64.getDecoder().decode(parts[1]);

		byte[] hash = pbkdf2(password.toCharArray(), salt);
		return MessageDigest.isEqual(hash, expectedHash); // Constant-time comparison
	}

	private static byte[] pbkdf2(char[] password, byte[] salt) throws Exception {
		PBEKeySpec spec = new PBEKeySpec(password, salt, ITERATIONS, KEY_LENGTH);
		SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
		return skf.generateSecret(spec).getEncoded();
	}
}
