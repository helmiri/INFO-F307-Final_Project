package be.ac.ulb.infof307.g06.models;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class Hash {

    private byte[] createChecksum(String filename) throws Exception {
        InputStream fis =  new FileInputStream(filename);

        byte[] buffer = new byte[1024];
        MessageDigest complete = MessageDigest.getInstance("MD5");
        int numRead;

        do {
            numRead = fis.read(buffer);
            if (numRead > 0) {
                complete.update(buffer, 0, numRead);
            }
        } while (numRead != -1);

        fis.close();
        return complete.digest();
    }

    /**
     * Return a hash for a file.
     * @param filename String
     * @return String
     * @throws Exception to change
     */
    public String hashFile(String filename) throws Exception {
        byte[] b = createChecksum(filename);
        String result = "";

        for (int i=0; i < b.length; i++) {
            result += Integer.toString( ( b[i] & 0xff ) + 0x100, 16).substring( 1 );
        }
        return result;
    }

    /**
     * Return a 32 bytes hash password with the raw password and a salt (unique for each user).
     * @param password String
     * @param salt String
     * @return String
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
        }
        catch (InvalidKeySpecException | NoSuchAlgorithmException e) {return "";}
    }
}
