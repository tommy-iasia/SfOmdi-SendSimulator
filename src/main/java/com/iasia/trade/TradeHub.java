package com.iasia.trade;

import com.iasia.hub.Hub;
import com.iasia.net.ChannelGroup;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Random;
import java.util.stream.Collectors;

public class TradeHub implements Hub {

    public TradeHub(ChannelGroup channelGroup, TradeStart... tradeStarts) {
        this.channelGroup = channelGroup;

        lastAdds = Arrays.stream(tradeStarts).map(t ->
                new AddTradeMessage(t.code, t.id, t.priceRaised, 0, 0))
                .collect(Collectors.toCollection(LinkedList::new));
    }
    private final ChannelGroup channelGroup;

    private final Random random = new Random(1);
    private final LinkedList<AddTradeMessage> lastAdds;
    private final LinkedList<CancelTradeMessage> cancels = new LinkedList<>();
    @Override
    public boolean run() throws IOException {
        for (var i = 0; i < 10; i++) {
            var lastTrade = lastAdds.poll();
            assert lastTrade != null;

            var changeRaised = random.nextInt(3) * 10;
            var quantity = random.nextInt(10) * 100;

            var nextTrade = new AddTradeMessage(
                    lastTrade.code,
                    lastTrade.id + 1,
                    lastTrade.priceRaised + changeRaised,
                    quantity,
                    System.currentTimeMillis());

            lastAdds.add(nextTrade);
            channelGroup.add(nextTrade);

            if (random.nextInt(10) == 1) {
                var cancel = new CancelTradeMessage(nextTrade.code, nextTrade.id);
                cancels.add(cancel);
            }
        }

        if (cancels.size() >= 10 || random.nextBoolean()) {
            channelGroup.addAll(cancels);
            cancels.clear();
        }

        channelGroup.send();
        return true;
    }
}
