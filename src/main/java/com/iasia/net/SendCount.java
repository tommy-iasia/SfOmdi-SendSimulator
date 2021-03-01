package com.iasia.net;

import java.util.List;
import java.util.stream.Stream;

public class SendCount {

    public final long length;
    public final long count;

    public SendCount(long length, long count) {
        this.length = length;
        this.count = count;
    }

    public static SendCount sum(List<SendCount> counts) {
        return new SendCount(
                counts.stream().mapToLong(t -> t.length).sum(),
                counts.stream().mapToLong(t -> t.count).sum());
    }
}
