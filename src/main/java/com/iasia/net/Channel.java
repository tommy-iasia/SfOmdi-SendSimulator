package com.iasia.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.DatagramChannel;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class Channel {

    public Channel(InetSocketAddress address) throws IOException {
        channel = DatagramChannel.open();

        channel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
        channel.setOption(StandardSocketOptions.SO_SNDBUF, 10 * 1024 * 1024);

        channel.connect(address);
    }

    private final List<Message> messages = new LinkedList<>();
    public void add(Message message) {
        messages.add(message);
    }

    public int nextSequence = 1;
    private ByteBuffer nextPacket() {
        var contentBuffers = messages.stream().map(Message::getContent).collect(Collectors.toList());

        var packetSize = 16 + contentBuffers.stream().mapToInt(t -> 2 + t.limit()).sum();
        var packetBuffer = ByteBuffer.allocate(packetSize).order(ByteOrder.LITTLE_ENDIAN);

        packetBuffer.putShort((short) packetSize);
        packetBuffer.put((byte) contentBuffers.size());
        packetBuffer.put((byte) 77);
        packetBuffer.putInt(nextSequence);
        packetBuffer.putLong(new Date().getTime());

        for (var contentBuffer : contentBuffers) {
            packetBuffer.putShort((short) (2 + contentBuffer.limit()));
            packetBuffer.put(contentBuffer);
        }

        var resetMessages = messages.stream()
                .filter(t -> t instanceof ResetSequenceMessage)
                .map(t -> (ResetSequenceMessage)t)
                .collect(Collectors.toList());

        if (resetMessages.isEmpty()) {
            nextSequence += contentBuffers.size();
        } else {
            var index = resetMessages.size() - 1;
            var resetMessage = resetMessages.get(index);
            nextSequence = resetMessage.sequence;
        }

        messages.clear();

        packetBuffer.flip();
        return packetBuffer;
    }

    private final DatagramChannel channel;
    public void send() throws IOException {
        var packet = nextPacket();
        sent += channel.write(packet);
    }

    private long sent = 0;
    public long sent() {
        return sent;
    }
}
