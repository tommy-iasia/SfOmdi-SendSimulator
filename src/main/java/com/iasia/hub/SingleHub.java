package com.iasia.hub;

import com.iasia.net.ChannelGroup;
import com.iasia.net.Message;

import java.io.IOException;

public abstract class SingleHub  implements Hub {

    public SingleHub(ChannelGroup channelGroup) throws IOException {
        this.channelGroup = channelGroup;
    }
    private final ChannelGroup channelGroup;

    @Override
    public boolean run() throws IOException {
        var message = next();
        if (message == null) {
            return false;
        }

        channelGroup.add(message);

        channelGroup.send();

        return true;
    }
    protected abstract Message next();

    @Override
    public long sent() {
        return channelGroup.sent();
    }
}