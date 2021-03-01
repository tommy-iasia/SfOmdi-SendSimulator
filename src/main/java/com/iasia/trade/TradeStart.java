package com.iasia.trade;

public class TradeStart {

    public final int code;
    public final int id;
    public final int priceRaised;

    public TradeStart(int code, int id, int priceRaised) {
        this.code = code;
        this.id = id;
        this.priceRaised = priceRaised;
    }
}
