package com.iasia;

import com.iasia.hub.Hub;
import com.iasia.market.MarketDefinitionHub;
import com.iasia.net.ChannelGroup;
import com.iasia.net.SendCount;
import com.iasia.security.SecurityDefinitionHub;
import com.iasia.time.Stopwatch;
import com.iasia.trade.AddTradeMessage;
import com.iasia.trade.TradeHub;
import com.iasia.trade.TradeStart;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class Program {

    public static void main(String[] args) throws IOException {
        System.out.println("start");

        sendDefinitions();
        sendDynamicData(2 * 1000);

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
        var tradeHub1 = new TradeHub(channel1,
                new TradeStart(1, 1_000_000, 50 * AddTradeMessage.PRICE_RAISE),
                new TradeStart(2, 2_000_000, 70 * AddTradeMessage.PRICE_RAISE));

        var channel2 = ChannelGroup.get(31);
        var tradeHub2 = new TradeHub(channel2,
                new TradeStart(3, 3_000_000, 10 * AddTradeMessage.PRICE_RAISE),
                new TradeStart(4, 4_000_000, 20 * AddTradeMessage.PRICE_RAISE),
                new TradeStart(5, 5_000_000, 50 * AddTradeMessage.PRICE_RAISE));

        var channel3 = ChannelGroup.get(32);
        var tradeHub3 = new TradeHub(channel3,
                new TradeStart(5, 5_000_000, 50 * AddTradeMessage.PRICE_RAISE));

        run(new Hub[] { tradeHub1, tradeHub2, tradeHub3 },
                new ChannelGroup[] { channel1, channel2, channel3 },
                timeout);
    }

    private static final long bandwidth = 5 * 1024 * 1024 / 8;
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
