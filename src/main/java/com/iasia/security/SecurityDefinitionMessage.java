package com.iasia.security;

import com.iasia.net.Message;
import com.iasia.text.Ascii8;
import com.iasia.text.Utf16Le;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class SecurityDefinitionMessage extends Message {

    public final int code;
    public final String market;
    public final String name;
    public final String currency;

    public SecurityDefinitionMessage(int code, String market, String name, String currency) {
        super((short) 11);

        this.code = code;
        this.market = market;
        this.name = name;
        this.currency = currency;
    }

    @Override
    public ByteBuffer getContent() {
        var buffer = ByteBuffer.allocate(544).order(ByteOrder.LITTLE_ENDIAN);
        buffer.putShort(type);

        buffer.putInt(code);
        Ascii8.put(buffer, market, 4);

        Ascii8.put(buffer, "empty", 12 + 4 + 1 + 1 + 2);

        Ascii8.put(buffer, name, 40);
        Ascii8.put(buffer, currency, 3);

        Utf16Le.put(buffer, "冇中文" + name, 60);
        Utf16Le.put(buffer, "簡體字係俾文盲睇既", 60);

        Ascii8.put(buffer, "empty", buffer.remaining());

        return buffer.flip();
    }
}
