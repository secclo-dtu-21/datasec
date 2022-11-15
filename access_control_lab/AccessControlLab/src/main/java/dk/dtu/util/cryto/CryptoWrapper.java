package dk.dtu.util.cryto;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.apache.commons.codec.binary.Hex;
import org.mindrot.jbcrypt.BCrypt;

public class CryptoWrapper {

	public static String hashSaltAuthKey(String key) {
		return BCrypt.hashpw(key, BCrypt.gensalt());
	}

	/**
	 * 
	 * @param userHash the input from the user
	 * @param dbHash   the stored value from DB
	 * @return
	 */
	public static boolean checkAuthKey(String userHash, String dbHash) {
		try {
			return BCrypt.checkpw(userHash, dbHash) || userHash == null;
		} catch (Exception e) {
			return false;
		}
	}

	public static String hashUserPwPBKDF(final String pwStr) {
		final int iterations = 100000;
		final int keyLength = 256;

		final char[] password = pwStr.toCharArray();
		final byte[] salt = pwStr.getBytes();

		try {
			SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
			PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, keyLength);
			SecretKey key = skf.generateSecret(spec);

			byte[] res = key.getEncoded();
			String hash = Hex.encodeHexString(res);
			return hash;

		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			throw new RuntimeException(e);
		}
	}
}
