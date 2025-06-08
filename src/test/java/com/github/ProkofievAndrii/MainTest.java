package com.github.ProkofievAndrii;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {

    @Test
    void givenMessage_shouldEncodeToHexString() {
        CreateProduct message = new CreateProduct("test", 100, true);
        String expected = "13010000000000000001000000405BDA502B6BCA34D09EB49B44606CB4D854F61302F54DBA7BFD8840E1CEF6D824840495AA931D77D5774134D8ABD45A9318C2B09C3683E35FDE5C0CBE897DFA74C4D93D320000";
        byte[] out = Main.encode(message);
        assertEquals(expected, Main.bytesToHex(out));
    }

    @Test
    void givenInvalidMessage_shouldThrowException() {
        byte[] in = new byte[] {29, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 37, 91, -38, 0, 0, 0, 3, 0, 0, 0, 4, 123, 34, 110, 97, 109, 101, 34, 58, 34, 116, 101, 115, 116, 34, 44, 34, 112, 114, 105, 99, 101, 34, 58, 49, 48, 48, 46, 48, 125, 0, 0};
        assertThrows(IllegalArgumentException.class, () -> Main.decode(in));
    }

    @Test
    public void testEncryptionDecryption() throws Exception {
        String message = "{\"msg\":\"Secret\"}";
        byte[] original = message.getBytes(StandardCharsets.UTF_8);
        byte[] encrypted = CypherUtils.encrypt(original);
        byte[] decrypted = CypherUtils.decrypt(encrypted);
        assertArrayEquals(original, decrypted);
    }
}