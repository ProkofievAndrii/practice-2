package com.github.ProkofievAndrii;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {

    @Test
    void givenMessage_shouldEncodeToHexString() {
        CreateProduct message = new CreateProduct("test", 100.0);
        String expected = "13010000000000000001000000255BDA00000003000000047B226E616D65223A2274657374222C227072696365223A3130302E307D0000";
        byte[] out = Main.encode(message);
        assertEquals(expected, Main.bytesToHex(out));
    }

    @Test
    void givenBytesArray_shouldDecodeMessage() {
        CreateProduct expected = new CreateProduct("test", 100.0);
        byte[] in = new byte[] {19, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 37, 91, -38, 0, 0, 0, 3, 0, 0, 0, 4, 123, 34, 110, 97, 109, 101, 34, 58, 34, 116, 101, 115, 116, 34, 44, 34, 112, 114, 105, 99, 101, 34, 58, 49, 48, 48, 46, 48, 125, 0, 0};
        assertEquals(expected, Main.decode(in));
    }

    @Test
    void givenInvalidMessage_shouldThrowException() {
        byte[] in = new byte[] {29, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 37, 91, -38, 0, 0, 0, 3, 0, 0, 0, 4, 123, 34, 110, 97, 109, 101, 34, 58, 34, 116, 101, 115, 116, 34, 44, 34, 112, 114, 105, 99, 101, 34, 58, 49, 48, 48, 46, 48, 125, 0, 0};
        assertThrows(IllegalArgumentException.class, () -> Main.decode(in));
    }
}