package co.jht.generaor;

import java.security.SecureRandom;
import java.util.Base64;

public class SecretKeyGenerator {

    public static void main(String[] args) {
        SecureRandom secureRandom = new SecureRandom();
        byte[] key = new byte[32]; // 256 bits
        secureRandom.nextBytes(key);
        String encodedKey = Base64.getUrlEncoder().withoutPadding().encodeToString(key);
        System.out.println("Generated secret key: " + encodedKey);
        // implement the secure industry standard to generating a secret that will be used for JWT authentication
    }
}