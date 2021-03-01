package com.iasia.net;

import com.iasia.collection.Pair;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.DatagramChannel;
import java.util.Collection;
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

    private final LinkedList<Message> messages = new LinkedList<>();
    public void add(Message message) {
        messages.add(message);
    }
    public void addAll(Collection<? extends Message> messages) {
        this.messages.addAll(messages);
    }

    public int nextSequence = 1;
    private ByteBuffer nextPacket() {
        var messageContents = nextContents();

        var packetSize = 16 + messageContents.stream().mapToInt(t -> 2 + t.b.limit()).sum();
        var packetBuffer = ByteBuffer.allocate(packetSize).order(ByteOrder.LITTLE_ENDIAN);

        packetBuffer.putShort((short) packetSize);
        packetBuffer.put((byte) messageContents.size());
        packetBuffer.put((byte) 77);
        packetBuffer.putInt(nextSequence);
        packetBuffer.putLong(System.currentTimeMillis());

        for (var messageContent : messageContents) {
            packetBuffer.putShort((short) (2 + messageContent.b.limit()));
            packetBuffer.put(messageContent.b);
        }

        var resetMessages = messageContents.stream()
                .filter(t -> t.a instanceof ResetSequenceMessage)
                .map(t -> (ResetSequenceMessage)t.a)
                .collect(Collectors.toList());

        if (resetMessages.isEmpty()) {
            nextSequence += messageContents.size();
        } else {
            var index = resetMessages.size() - 1;
            var resetMessage = resetMessages.get(index);
            nextSequence = resetMessage.sequence;
        }

        packetBuffer.flip();
        return packetBuffer;
    }
    private List<Pair<Message, ByteBuffer>> nextContents() {
        var pairs = new LinkedList<Pair<Message, ByteBuffer>>();
        var length = 0;

        Message message;
        while ((message = messages.poll()) != null) {
            var content = message.getContent();

            var pair = new Pair<>(message, content);
            pairs.add(pair);

            length += 2 + content.limit();
            if (length >= 1000) {
                break;
            }
        }

        return pairs;
    }

    private final DatagramChannel channel;
    public void send() throws IOException {
        while (!messages.isEmpty()) {
            var packet = nextPacket();

            sendLength += channel.write(packet);

            sendCount++;
        }
    }
    private long sendLength = 0;
    private long sendCount = 0;
    public SendCount sent() {
        return new SendCount(sendLength, sendCount);
    }
}
