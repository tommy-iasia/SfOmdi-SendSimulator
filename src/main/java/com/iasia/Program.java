package com.iasia;

import com.iasia.hub.Hub;
import com.iasia.market.MarketDefinitionHub;
import com.iasia.net.ChannelGroup;
import com.iasia.security.SecurityDefinitionHub;
import com.iasia.time.Stopwatch;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.stream.Collectors;

public class Program {

    public static void main(String[] args) throws IOException {
        System.out.println("start");

        var channel1 = ChannelGroup.get(1);

        var marketDefinitionHub = new MarketDefinitionHub(channel1);
        var securityDefinitionHub = new SecurityDefinitionHub(channel1);
        run(marketDefinitionHub, securityDefinitionHub);

        System.out.println("end");
    }

    private static long bandwidth = 20 * 1024 * 1024 / 8;
    private static void run(Hub... hubs) {
        System.out.println("run starts");
        for (var hub : hubs) {
            System.out.println("hub " + hub + " starts");
        }

        var stopwatch = Stopwatch.start();

        var remainingHubs = new LinkedList<>(Arrays.asList(hubs));
        while (remainingHubs.size() > 0) {
            var allowed = bandwidth * stopwatch.elapsed() / 1000;
            var sent = remainingHubs.stream().mapToLong(Hub::sent).sum();
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

        var totalSent = Arrays.stream(hubs).mapToLong(Hub::sent).sum();
        System.out.println("sent " + totalSent / 1024 / 1024 + "MB / " + totalSent + "B");
    }
}
