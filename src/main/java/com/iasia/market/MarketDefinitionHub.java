package com.iasia.market;

import com.iasia.hub.SingleHub;
import com.iasia.net.ChannelGroup;
import com.iasia.net.Message;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

public class MarketDefinitionHub extends SingleHub {

    public MarketDefinitionHub(ChannelGroup channelGroup) throws IOException {
        super(channelGroup);
    }

    private Queue<MarketDefinitionMessage> messages = new LinkedList<>(Arrays.asList(
            new MarketDefinitionMessage("MAIN", "main board", "HKD", 5),
            new MarketDefinitionMessage("GEM", "get everybody moving", "HKD", 0)
    ));

    @Override
    protected Message next() {
        return messages.poll();
    }
}
