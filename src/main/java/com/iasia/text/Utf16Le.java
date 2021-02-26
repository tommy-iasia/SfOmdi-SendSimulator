package com.iasia.text;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class Utf16Le {

    public static ByteBuffer put(ByteBuffer buffer, String text, int length) {
        var bytes = text.getBytes(StandardCharsets.UTF_16LE);

        if (bytes.length == length) {
            return buffer.put(bytes);
        } else if (bytes.length > length) {
            return buffer.put(bytes, 0, length);
        } else {
            buffer.put(bytes);

            for (var i = bytes.length; i < length; i++) {
                buffer.put((byte) 0);
            }

            return buffer;
        }
    }
}
