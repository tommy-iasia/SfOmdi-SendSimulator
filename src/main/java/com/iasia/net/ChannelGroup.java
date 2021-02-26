package com.iasia.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ChannelGroup {

    public ChannelGroup(InetSocketAddress... address) {
        channels = Arrays.stream(address).map(t -> {
            try {
                return new Channel(t);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());
    }
    private final List<Channel> channels;

    public void add(Message message) {
        for (var channel : channels) {
            channel.add(message);
        }
    }

    public void send() throws IOException {
        for (var channel : channels) {
            channel.send();
        }
    }
    public long sent() {
        return channels.stream().mapToLong(t -> t.sent()).sum();
    }

    public static ChannelGroup get(int channelId) throws IOException {
        var addresses = new LinkedList<InetSocketAddress>();

        //var path = Paths.get("config/standard.udp.config");
        var path = Paths.get("config/fulltick.udp.config");
        var text = Files.readString(path);

        var pattern = Pattern.compile("(?<id>\\d+)[A-Z] (?<host>\\d+\\.\\d+\\.\\d+\\.\\d+):(?<port>\\d+)");
        var matcher = pattern.matcher(text);

        while (matcher.find()) {
            var idText = matcher.group("id");
            var idValue = Integer.parseInt(idText);
            if (idValue != channelId) {
                continue;
            }

            var host = matcher.group("host");

            var portText = matcher.group("port");
            var portValue = Integer.parseInt(portText);

            var address = new InetSocketAddress(host, portValue);
            addresses.add(address);
        }

        if (addresses.isEmpty()) {
            throw new IllegalArgumentException();
        }

        return new ChannelGroup(addresses.toArray(new InetSocketAddress[0]));
    }
}
