package com.iasia.net;

import java.nio.ByteBuffer;

public abstract class Message {

    public Message(short type) {
        this.type = type;
    }
    public final short type;

    public abstract ByteBuffer getContent();
}
