package com.iasia.market;

import com.iasia.net.Message;
import com.iasia.text.Ascii8;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class MarketDefinitionMessage extends Message {

    public final String code;
    public final String name;
    public final String currency;
    public final int securityCount;

    public MarketDefinitionMessage(String code, String name, String currency, int securityCount) {
        super((short) 10);

        this.code = code;
        this.name = name;
        this.currency = currency;
        this.securityCount = securityCount;
    }

    @Override
    public ByteBuffer getContent() {
        var buffer = ByteBuffer.allocate(38).order(ByteOrder.LITTLE_ENDIAN);
        buffer.putShort(type);

        Ascii8.put(buffer, code, 4);
        Ascii8.put(buffer, name, 25);
        Ascii8.put(buffer, currency, 3);
        buffer.putInt(securityCount);

        return buffer.flip();
    }
}
