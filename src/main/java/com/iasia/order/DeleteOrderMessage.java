package com.iasia.order;

import com.iasia.net.Message;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class DeleteOrderMessage extends Message {

    public DeleteOrderMessage(int code, long id, OrderSide side) {
        super((short) 50);

        this.code = code;
        this.id = id;
        this.side = side;
    }

    public final int code;
    public final long id;

    public final OrderSide side;

    @Override
    public ByteBuffer getContent() {
        var buffer = ByteBuffer.allocate(38).order(ByteOrder.LITTLE_ENDIAN);
        buffer.putShort(type);

        buffer.putInt(code);
        buffer.putLong(id);

        buffer.putShort((short) (side == OrderSide.Bid ? 0 : 1));

        buffer.put((byte) '7');
        buffer.put((byte) '7');

        return buffer.flip();
    }
}
