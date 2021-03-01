package com.iasia.order;

public class Order {

    public final long id;
    public final int priceRaised;
    public final int quantity;
    public final OrderSide side;

    public Order(long id, int priceRaised, int quantity, OrderSide side) {
        this.id = id;
        this.priceRaised = priceRaised;
        this.quantity = quantity;
        this.side = side;
    }
}
