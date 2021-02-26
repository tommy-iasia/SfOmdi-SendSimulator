package com.iasia.market;

import com.iasia.hub.SingleHub;
import com.iasia.net.ChannelGroup;
import com.iasia.net.Message;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class MarketDefinitionHub extends SingleHub {

    public MarketDefinitionHub(ChannelGroup channelGroup) {
        super(channelGroup);
    }

    public final static List<MarketDefinitionMessage> markets = Arrays.asList(
            new MarketDefinitionMessage("MAIN", "main board", "HKD", 5),
            new MarketDefinitionMessage("GEM", "get everybody moving", "HKD", 0));

    private Queue<MarketDefinitionMessage> messages = new LinkedList<>(markets);
    @Override
    protected Message next() {
        return messages.poll();
    }
}
