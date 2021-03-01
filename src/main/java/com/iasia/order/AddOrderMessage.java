package com.iasia.order;

import com.iasia.net.Message;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class AddOrderMessage extends Message {

    public AddOrderMessage(int code, long id, int priceRaised, int quantity, OrderSide side) {
        super((short) 30);

        this.code = code;
        this.id = id;
        this.priceRaised = priceRaised;
        this.quantity = quantity;
        this.side = side;
    }

    public final int code;
    public final long id;

    public final int priceRaised;
    public final int quantity;

    public final OrderSide side;

    @Override
    public ByteBuffer getContent() {
        var buffer = ByteBuffer.allocate(38).order(ByteOrder.LITTLE_ENDIAN);
        buffer.putShort(type);

        buffer.putInt(code);
        buffer.putLong(id);

        buffer.putInt(priceRaised);
        buffer.putInt(quantity);

        buffer.putShort((short) (side == OrderSide.BID ? 0 : 1));
        buffer.put((byte) '1');

        buffer.put((byte) '7');
        buffer.putInt(0);

        return buffer.flip();
    }
}

