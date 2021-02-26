package com.iasia.trade;

import com.iasia.hub.Hub;
import com.iasia.net.ChannelGroup;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Random;
import java.util.stream.Collectors;

public class TradeHub implements Hub {

    public TradeHub(ChannelGroup channelGroup, TradeStartPoint... startPoints) {
        this.channelGroup = channelGroup;

        lastTrades = new LinkedList<>(Arrays.stream(startPoints).map(t ->
                new TradeAddMessage(t.code, t.id, t.priceRaised, 0, 0))
                .collect(Collectors.toList()));
    }
    private final ChannelGroup channelGroup;

    private final Random random = new Random(1);
    private final LinkedList<TradeAddMessage> lastTrades;
    @Override
    public boolean run() throws IOException {
        for (var i = 0; i < 10; i++) {
            for (var j = 0; j < 10; j++) {
                var lastTrade = lastTrades.poll();

                var changeRaised = random.nextInt(3) * 10;
                var quantity = random.nextInt(10) * 100;

                var nextTrade = new TradeAddMessage(lastTrade.code,
                        lastTrade.id + 1,
                        lastTrade.priceRaised + changeRaised,
                        quantity,
                        System.currentTimeMillis());

                lastTrades.add(nextTrade);

                channelGroup.add(nextTrade);
            }

            channelGroup.send();
        }

        return true;
    }
}
