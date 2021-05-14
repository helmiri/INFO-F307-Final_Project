package be.ac.ulb.infof307.g06.models.encryption;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestHash {
    private List<String> testData;

    @BeforeAll
    public void setup() {
        testData = new ArrayList<>();
        // Generate a random array of strings to be used as passwords
        for (int i = 0; i < 10; i++) {
            byte[] array = new byte[10]; // Generate strings of size 10
            new Random().nextBytes(array);
            testData.add(new String(array, StandardCharsets.UTF_8));
        }
    }

    @Test
    @DisplayName("Hashes returned by the same parameters are equal")
    public void testHashPassword() {
        Hash hash1 = new Hash();
        Hash hash2 = new Hash();
        for (String testPassword : testData) {
            String password1 = hash1.hashPassword(testPassword, "An Interesting Salt");
            String password2 = hash2.hashPassword(testPassword, "An Interesting Salt");
            assertEquals(password1, password2);
        }
    }

    @Test
    @DisplayName("Hash is different from password")
    public void testHashDifferentThanPassword() {
        Hash hash1 = new Hash();
        for (String testPassword : testData) {
            String password1 = hash1.hashPassword(testPassword, "An Interesting Salt");
            assertNotEquals(testPassword, password1);
        }
    }
}
