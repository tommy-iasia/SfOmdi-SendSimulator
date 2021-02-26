package com.iasia.time;

public class Stopwatch {

    private Stopwatch(long start) {
        this.start = start;
    }
    private final long start;

    public static Stopwatch start() {
        var start = System.currentTimeMillis();

        return new Stopwatch(start);
    }
    public long elapsed() {
        var time = System.currentTimeMillis();

        return time - start;
    }
}
