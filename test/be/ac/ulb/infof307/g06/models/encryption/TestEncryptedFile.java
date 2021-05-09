package be.ac.ulb.infof307.g06.models.encryption;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestEncryptedFile {
    private String testFilePath = "test/be/ac/ulb/infof307/g06/models/encryption/testFile.txt";
    String decryptedTestFilePath = testFilePath + "decrypted";
    private String encryptedTestFilePath = testFilePath + "encrypted";

    @BeforeEach
    public void setup() throws IOException {
        FileWriter testFile = new FileWriter(testFilePath);
        String testString = "Test String to ensure that the encryption and decryption methods are done correctly";
        testFile.write(testString);
        testFile.close();
    }

    @AfterEach
    public void cleanup() {
        new File(testFilePath).delete();
        new File(encryptedTestFilePath).delete();
        new File(decryptedTestFilePath).delete();
    }

    public void encryptFile(String password) throws IOException {
        EncryptedFile file = new EncryptedFile(password, testFilePath);
        file.encryptFile(encryptedTestFilePath);
    }

    /**
     * @return true if equal, false otherwise
     * @throws IOException on error reading the files
     */
    public boolean compareContents(String file1, String file2) throws IOException {
        File fileTest = new File(file1);
        File encryptedTest = new File(file2);
        byte[] testFileContent = Files.readAllBytes(fileTest.toPath());
        byte[] encryptedFileContent = Files.readAllBytes(encryptedTest.toPath());

        boolean check = true;
        for (int i = 0; i < testFileContent.length; i++) {
            if (testFileContent[i] != encryptedFileContent[i]) {
                check = false;
                break;
            }
        }
        return check;
    }

    @Test
    @DisplayName("Encryption test")
    public void testFileEncrypt() throws IOException {
        encryptFile("My password");
        assertFalse(compareContents(testFilePath, encryptedTestFilePath));
    }

    @Test
    @DisplayName("Decryption Test")
    public void testFileDecrypt() throws IOException {
        encryptFile("My Password");
        EncryptedFile file = new EncryptedFile("My Password", encryptedTestFilePath);
        file.decryptFile(decryptedTestFilePath);

        assertTrue(compareContents(testFilePath, decryptedTestFilePath));
    }

    @Test
    @DisplayName("Wrong password Decryption Test")
    public void testFileWrongPasswordDecrypt() throws IOException {
        encryptFile("My Password");
        EncryptedFile file = new EncryptedFile("Wrong password", encryptedTestFilePath);
        file.decryptFile(decryptedTestFilePath);
        assertFalse(compareContents(testFilePath, decryptedTestFilePath));
    }
}
