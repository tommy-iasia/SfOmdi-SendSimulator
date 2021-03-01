package com.iasia.trade;

import com.iasia.net.Message;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class CancelTradeMessage extends Message {

    public CancelTradeMessage(int code, int id) {
        super((short) 51);

        this.code = code;
        this.id = id;
    }

    public final int code;
    public final int id;

    @Override
    public ByteBuffer getContent() {
        var buffer = ByteBuffer.allocate(12).order(ByteOrder.LITTLE_ENDIAN);
        buffer.putShort(type);

        buffer.putInt(code);
        buffer.putInt(id);

        return buffer.flip();
    }
}
