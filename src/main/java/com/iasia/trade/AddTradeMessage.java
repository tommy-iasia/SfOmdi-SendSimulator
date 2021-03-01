package com.iasia.trade;

import com.iasia.net.Message;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class AddTradeMessage extends Message {

    public AddTradeMessage(int code, int id, int priceRaised, int quantity, long time) {
        super((short) 50);

        this.code = code;
        this.id = id;
        this.priceRaised = priceRaised;
        this.quantity = quantity;
        this.time = time;
    }

    public final int code;
    public final int id;

    public final int priceRaised;
    public final static int PRICE_RAISE = 1_000;

    public final int quantity;

    public final long time;

    @Override
    public ByteBuffer getContent() {
        var buffer = ByteBuffer.allocate(30).order(ByteOrder.LITTLE_ENDIAN);
        buffer.putShort(type);

        buffer.putInt(code);
        buffer.putInt(id);

        buffer.putInt(priceRaised);
        buffer.putInt(quantity);

        buffer.putShort((short) 0);

        buffer.put((byte) '7');
        buffer.put((byte) '7');

        buffer.putLong(time);

        return buffer.flip();
    }
}
