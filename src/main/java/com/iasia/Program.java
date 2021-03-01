package com.iasia;

import com.iasia.hub.Hub;
import com.iasia.market.MarketDefinitionHub;
import com.iasia.net.ChannelGroup;
import com.iasia.net.SendCount;
import com.iasia.order.OrderHub;
import com.iasia.security.SecurityDefinitionHub;
import com.iasia.time.Stopwatch;
import com.iasia.trade.AddTradeMessage;
import com.iasia.trade.TradeHub;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class Program {

    public static void main(String[] args) throws IOException {
        System.out.println("start");

        sendDefinitions();
        sendDynamicData(1 * 1000);

        var allSent = SendCount.sum(totalSends);
        System.out.println("sent "
                + allSent.length / 1024 / 1024 + "MB / "
                + allSent.length / 1024 + "KB / "
                + allSent.length + "B / "
                + allSent.count / 1000 + "kp / "
                + allSent.count + "p");

        System.out.println("end");
    }

    private static void sendDefinitions() throws IOException {
        var channel = ChannelGroup.get(1);

        var marketDefinitionHub = new MarketDefinitionHub(channel);
        var securityDefinitionHub = new SecurityDefinitionHub(channel);

        run(new Hub[] { marketDefinitionHub, securityDefinitionHub},
                new ChannelGroup[] { channel },
                Long.MAX_VALUE);
    }

    private static void sendDynamicData(long timeout) throws IOException {
        var channel1 = ChannelGroup.get(30);

        var tradeHub1 = new TradeHub(1, 50 * AddTradeMessage.PRICE_RAISE, channel1);
        var orderHub1 = new OrderHub(tradeHub1, channel1);

        var tradeHub2 = new TradeHub(2, 70 * AddTradeMessage.PRICE_RAISE, channel1);
        var orderHub2 = new OrderHub(tradeHub2, channel1);

        var channel2 = ChannelGroup.get(31);

        var tradeHub3 = new TradeHub(3, 10 * AddTradeMessage.PRICE_RAISE, channel2);
        var orderHub3 = new OrderHub(tradeHub3, channel2);

        var tradeHub4 = new TradeHub(4, 20 * AddTradeMessage.PRICE_RAISE, channel2);
        var orderHub4 = new OrderHub(tradeHub4, channel2);

        var tradeHub5 = new TradeHub(5, 50 * AddTradeMessage.PRICE_RAISE, channel2);
        var orderHub5 = new OrderHub(tradeHub5, channel2);

        run(
                new Hub[] {
                        orderHub1, orderHub2, orderHub3, orderHub4, orderHub5,
                        tradeHub1, tradeHub2, tradeHub3, tradeHub4, tradeHub5,
                },
                new ChannelGroup[] { channel1, channel2 },
                timeout);
    }

    private static final long bandwidth = 60 * 1024 * 1024 / 8;
    private static final List<SendCount> totalSends = new LinkedList<>();
    private static void run(Hub[] hubs, ChannelGroup[] channelGroups, long timeout) {
        System.out.println("run starts");
        for (var hub : hubs) {
            System.out.println("hub " + hub + " starts");
        }

        var remainingHubs = new LinkedList<>(Arrays.asList(hubs));
        var stopwatch = Stopwatch.start();

        while (remainingHubs.size() > 0
                && stopwatch.elapsed() < timeout) {

            var allowed = bandwidth * stopwatch.elapsed() / 1000;
            var sent = Arrays.stream(channelGroups).mapToLong(t -> t.sent().length).sum();
            if (allowed <= sent) {
                continue;
            }

            var finishedHubs = remainingHubs.stream()
                    .filter(t -> {
                        try {
                            return !t.run();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .collect(Collectors.toList());

            remainingHubs.removeAll(finishedHubs);

            for (var hub : finishedHubs) {
                System.out.println("hub " + hub + " finishes");
            }
        }

        var allSends = Arrays.stream(channelGroups).map(ChannelGroup::sent).collect(Collectors.toList());
        var allSent = SendCount.sum(allSends);
        System.out.println("sent "
                + allSent.length / 1024 / 1024 + "MB / "
                + allSent.length / 1024 + "KB / "
                + allSent.length + "B / "
                + allSent.count / 1000 + "kp / "
                + allSent.count + "p");

        totalSends.add(allSent);

        System.out.println("run ends");
    }
}
