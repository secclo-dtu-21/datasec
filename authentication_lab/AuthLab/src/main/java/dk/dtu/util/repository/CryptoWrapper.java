package dk.dtu.util.repository;

import org.mindrot.jbcrypt.BCrypt;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import de.mkammerer.argon2.Argon2Factory.Argon2Types;

public class CryptoWrapper {

	public static String hashUserPwPBKDF(String pw) {
		Argon2 argon2 = Argon2Factory.create(Argon2Types.ARGON2id);
		// OWASP recommendation:
		// iteration count = 2, memory = 15 MiB, degree of parallelism = 1
		String pwHash = argon2.hash(2, 15 * 1024, 1, pw.toCharArray());
		return pwHash;
	}

	public static String hashSaltAuthKey(String key) {
		return BCrypt.hashpw(key, BCrypt.gensalt());
	}

	/**
	 * 
	 * @param userHash the input from the user
	 * @param dbHash the stored value from DB
	 * @return
	 */
	public static boolean checkAuthKey(String userHash, String dbHash) {
		return BCrypt.checkpw(userHash, dbHash) || userHash == null
	}
}
