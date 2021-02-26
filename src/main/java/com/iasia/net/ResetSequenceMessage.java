package com.iasia.net;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ResetSequenceMessage extends Message {

    public ResetSequenceMessage(short type, int sequence) {
        super(type);

        this.sequence = sequence;
    }
    public final int sequence;

    @Override
    public ByteBuffer getContent() {
        var buffer = ByteBuffer.allocate(6).order(ByteOrder.LITTLE_ENDIAN);
        buffer.putShort(type);

        buffer.putInt(sequence);

        return buffer.flip();
    }
}
