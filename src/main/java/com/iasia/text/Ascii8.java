package com.iasia.text;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class Ascii8 {

    public static ByteBuffer put(ByteBuffer buffer, String text) {
        var bytes = text.getBytes(StandardCharsets.ISO_8859_1);
        return buffer.put(bytes);
    }
    public static ByteBuffer put(ByteBuffer buffer, String text, int length) {
        if (text.length() == length) {
            return put(buffer, text);
        } else if (text.length() > length) {
            var trimmed = text.substring(0, length);
            return put(buffer, trimmed);
        } else {
            put(buffer, text);

            for (var i = text.length(); i < length; i++) {
                buffer.put((byte) 0);
            }

            return buffer;
        }
    }
}
