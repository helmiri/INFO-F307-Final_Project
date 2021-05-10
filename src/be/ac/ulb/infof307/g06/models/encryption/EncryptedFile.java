package be.ac.ulb.infof307.g06.models.encryption;

import javax.crypto.*;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Random;



/**
 * Encrypts/Decrypts password protected files
 */
public class EncryptedFile {
    private final String algorithm = "PBEWithMD5AndTripleDES";
    private final String source;
    private PBEKeySpec pbeKeySpec;
    private String encrypted_source;

    /**
     *
     * @param password The password to be used for encryption/decryption
     * @param source   The source/encrypted file
     */
    public EncryptedFile(String password, String source) {
        this.source = source;
        pbeKeySpec = new PBEKeySpec(password.toCharArray());
    }

    /**
     * Encrypts a file using Password Based Encryption
     *
     * @param destination Path to the output file
     * @throws IOException On file access error
     */
    public void encryptFile(String destination) throws IOException {
        encrypted_source = destination;
        byte[] salt = generateSalt();
        PBEParameterSpec pbeParameterSpec = new PBEParameterSpec(salt, 100);

        FileInputStream inFile = new FileInputStream(source);
        FileOutputStream outFile = new FileOutputStream(destination);

        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(), pbeParameterSpec);
            outFile.write(salt);
        } catch (InvalidKeyException | InvalidAlgorithmParameterException | NoSuchPaddingException | NoSuchAlgorithmException ignored) {
            // The algorithm used for encryption/decryption ensure that these exceptions will never be triggered
        }
        processFile(inFile, outFile, cipher);
    }

    /**
     * Generates the secret key used in the cipher
     *
     * @return The secret key
     */
    private SecretKey getSecretKey() {
        SecretKey secretKey = null;
        try {
            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(algorithm);
            secretKey = secretKeyFactory.generateSecret(pbeKeySpec);
        } catch (InvalidKeySpecException | NoSuchAlgorithmException ignored) {
            // These exceptions will never be triggered and can safely be ignored because:
            // NoSuchAlgorithmException: triggered by SecretKeyFactory.getInstance when the algorithm is not valid.
            // We use PBEWithMD5AndTripleDES which is valid

            // InvalidKeySpecException: triggered by secretKeyFactory.generateSecret if the given key specification is inappropriate for this secret-key factory to produce a secret key.
            // We ensure that the key specification is appropriate
        }
        return secretKey;
    }

    /**
     * Generates an 8 byte random salt
     *
     * @return The salt in a byte array
     */
    private byte[] generateSalt() {
        byte[] salt = new byte[8];
        Random random = new Random();
        random.nextBytes(salt);
        return salt;
    }

    /**
     * Decrypts the file
     *
     * @param destination Path to output file
     * @throws IOException On error writing the file
     */
    public void decryptFile(String destination) throws IOException {
        // If null, encryptFile wasn't used and therefore, the source file provided has to be encrypted
        if (encrypted_source == null) {
            encrypted_source = source;
        }
        FileInputStream in = new FileInputStream(encrypted_source);
        ;
        FileOutputStream out = new FileOutputStream(destination);
        try {
            byte[] salt = new byte[8];
            in.read(salt);

            PBEParameterSpec pbeParameterSpec = new PBEParameterSpec(salt, 100);
            Cipher cipher = null;
            try {
                cipher = Cipher.getInstance(algorithm);
                cipher.init(Cipher.DECRYPT_MODE, getSecretKey(), pbeParameterSpec);
            } catch (InvalidAlgorithmParameterException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException ignored) {
                // The algorithm used for encryption/decryption ensure that these exceptions will never be triggered
            }
            processFile(in, out, cipher);
        } finally {
            in.close();
            out.flush();
            out.close();
        }
    }

    /**
     * Uses the cipher to encrypt or decrypt a file
     *
     * @param inFile  The input file
     * @param outFile The output path
     * @param cipher  Cipher used in decryption or encryption mode
     * @throws IOException On error reading/writing the file
     */
    private void processFile(FileInputStream inFile, FileOutputStream outFile, Cipher cipher) throws IOException {
        byte[] input = new byte[64];
        int bytesRead;
        while ((bytesRead = inFile.read(input)) != -1) {
            byte[] output = cipher.update(input, 0, bytesRead);
            if (output != null) {
                outFile.write(output);
            }
        }

        byte[] output = new byte[0];
        try {
            output = cipher.doFinal();
        } catch (IllegalBlockSizeException | BadPaddingException ignored) {
            // The cipher and algorithm used guarantee that this exception will never be triggered
        }
        if (output != null) {
            outFile.write(output);
        }
        inFile.close();
        outFile.flush();
        outFile.close();
    }
}