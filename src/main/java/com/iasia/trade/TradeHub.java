package com.iasia.trade;

import com.iasia.hub.Hub;
import com.iasia.net.ChannelGroup;
import com.iasia.order.OrderHub;

import java.io.IOException;
import java.util.Random;

public class TradeHub implements Hub {

    public TradeHub(int code, int priceRaised, ChannelGroup channelGroup) {
        this.code = code;
        this.channelGroup = channelGroup;

        lastTrade = new AddTradeMessage(code, code * 1_000_000, priceRaised, 0, 0);
    }
    public final int code;
    private final Random random = new Random(1);

    private AddTradeMessage lastTrade;
    public int lastPriceRaised() {
        return lastTrade.priceRaised;
    }

    public OrderHub orderHub;
    private final ChannelGroup channelGroup;
    @Override
    public boolean run() throws IOException {
        var orders = random.nextBoolean() ?
                orderHub.bestBids()
                : orderHub.bestOffers();

        if (orders.isEmpty()) {
            return true;
        }

        var time = System.currentTimeMillis();
        for (var order : orders) {
            lastTrade = new AddTradeMessage(
                    code, lastTrade.id + 1,
                    order.priceRaised, order.quantity,
                    time);

            channelGroup.add(lastTrade);
        }

        channelGroup.send();

        orderHub.runDeleteOrders(orders);

        return true;
    }
}
