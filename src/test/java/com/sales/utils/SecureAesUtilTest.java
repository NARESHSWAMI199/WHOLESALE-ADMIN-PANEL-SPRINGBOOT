package com.sales.utils;

import com.sales.exceptions.MyException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SecureAesUtilTest {

    @Test
    void testEncryptDecrypt_roundtrip() {
        String key = "0123456789abcdef"; // 16 bytes
        String plaintext = "hello world";

        String cipher = SecureAesUtil.encrypt(plaintext, key);
        assertNotNull(cipher);
        System.out.println("CIPHER='" + cipher + "'");
        // ensure base64 decoding works (fails with clear error if not)
        try {
            byte[] decoded = java.util.Base64.getDecoder().decode(cipher);
            assertTrue(decoded.length > 0);
        } catch (IllegalArgumentException iae) {
            fail("Cipher is not valid base64: " + cipher);
        }

        String decrypted = SecureAesUtil.decrypt(cipher, key);
        assertEquals(plaintext, decrypted);
    }

    @Test
    void testEncrypt_invalidKey_throws() {
        String shortKey = "short";
        assertThrows(MyException.class, () -> SecureAesUtil.encrypt("x", shortKey));
    }

    @Test
    void testDecrypt_invalidCipher_throws() {
        String key = "0123456789abcdef";
        assertThrows(MyException.class, () -> SecureAesUtil.decrypt("not-base64", key));
    }

}
