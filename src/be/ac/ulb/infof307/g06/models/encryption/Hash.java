package be.ac.ulb.infof307.g06.models.encryption;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;



/**
 * String hashing class
 */
public class Hash {

    /**
     * Return a 32 bytes hash password with the raw password and a salt (unique for each user).
     * @param password The user's password
     * @param salt Tye hash salt
     * @return The hashed password
     */
    public String hashPassword(String password, String salt) {
        try {
            final int ITERATIONS = 1000;
            final int KEY_LENGTH = 192; // bits
            char[] passwordChars = password.toCharArray();
            byte[] saltBytes = salt.getBytes();

            PBEKeySpec spec = new PBEKeySpec(
                    passwordChars,
                    saltBytes,
                    ITERATIONS,
                    KEY_LENGTH
            );
            SecretKeyFactory key = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            byte[] hashedPassword = key.generateSecret(spec).getEncoded();
            return String.format("%x", new BigInteger(hashedPassword));
        } catch (InvalidKeySpecException | NoSuchAlgorithmException error) {return "";}
    }
}
