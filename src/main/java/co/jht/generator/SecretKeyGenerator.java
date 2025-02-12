package co.jht.generator;

import java.security.SecureRandom;
import java.util.Base64;

public class SecretKeyGenerator {
    public static void main(String[] args) {
        SecureRandom rand = new SecureRandom();
        byte[] key = new byte[32]; // 256 bits
        rand.nextBytes(key);
        String encodedKey = Base64.getUrlEncoder().withoutPadding().encodeToString(key);
        System.out.println("Generated secret key: " + encodedKey);
    }
}