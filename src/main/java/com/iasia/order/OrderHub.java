package com.iasia.order;

import com.iasia.hub.Hub;
import com.iasia.net.ChannelGroup;
import com.iasia.trade.TradeHub;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.TreeSet;

public class OrderHub implements Hub {

    public OrderHub(TradeHub tradeHub, ChannelGroup channelGroup) {
        this.tradeHub = tradeHub;
        this.channelGroup = channelGroup;

        tradeHub.orderHub = this;
        nextId = tradeHub.code * 1_000_000_000L;
    }

    private final Random random = new Random(1);
    private final ChannelGroup channelGroup;

    @Override
    public boolean run() throws IOException {
        randomAddOrders(30);

        var excess = bidQueue.size() + offerQueue.size() - 10_000;
        if (excess > 0) {
            deleteWorstOrders(excess);
        }

        channelGroup.send();
        return true;
    }

    public final TradeHub tradeHub;
    private long nextId;
    private void randomAddOrders(int count) {
        for (var i = 0; i < count; i++) {
            if (random.nextBoolean()) {
                addBidOrder();
            } else {
                addOfferOrder();
            }
        }
    }

    private final TreeSet<Order> bidQueue = new TreeSet<>(new BidOrderComparator());
    public List<Order> bestBids() {
        if (bidQueue.isEmpty()) {
            return new LinkedList<>();
        }

        var firstOrder = bidQueue.first();
        var firstPrice = firstOrder.priceRaised;

        var orders = new LinkedList<Order>();
        for (Order order : bidQueue) {
            if (order.priceRaised >= firstPrice) {
                orders.add(order);
            } else {
                break;
            }
        }
        return orders;
    }
    private void addBidOrder() {
        var lastPrice = tradeHub.lastPriceRaised();
        var priceChange = random.nextInt(10) + 1;
        var orderPrice = Math.max(lastPrice - priceChange, 1);

        var quantity = (random.nextInt(2) + 1) * 100;

        var order = new Order(nextId++, orderPrice, quantity, OrderSide.BID);
        bidQueue.add(order);

        var add = new AddOrderMessage(tradeHub.code, order.id, orderPrice, quantity, OrderSide.BID);
        channelGroup.add(add);
    }

    private final TreeSet<Order> offerQueue = new TreeSet<>(new OfferOrderComparator());
    public List<Order> bestOffers() {
        if (offerQueue.isEmpty()) {
            return new LinkedList<>();
        }

        var firstOrder = offerQueue.first();
        var firstPrice = firstOrder.priceRaised;

        var orders = new LinkedList<Order>();
        for (Order order : offerQueue) {
            if (order.priceRaised <= firstPrice) {
                orders.add(order);
            } else {
                break;
            }
        }
        return orders;
    }
    private void addOfferOrder() {
        var lastPrice = tradeHub.lastPriceRaised();
        var priceChange = random.nextInt(10) + 1;
        var orderPrice = lastPrice + priceChange;

        var quantity = (random.nextInt(2) + 1) * 100;

        var order = new Order(nextId++, orderPrice, quantity, OrderSide.OFFER);
        offerQueue.add(order);

        var add = new AddOrderMessage(tradeHub.code, order.id, orderPrice, quantity, OrderSide.OFFER);
        channelGroup.add(add);
    }

    private void deleteWorstOrders(int count) {
        var orders = new LinkedList<Order>();

        for (var i = 0; i < count / 2; i++) {
            var bid = bidQueue.pollLast();
            if (bid != null) {
                orders.add(bid);
            }

            var offer = offerQueue.pollLast();
            if (offer != null) {
                orders.add(offer);
            }
        }

        deleteOrders(orders);
    }

    public void runDeleteOrders(List<Order> orders) throws IOException {
        deleteOrders(orders);

        channelGroup.send();
    }
    private void deleteOrders(List<Order> orders) {
        for (Order order : orders) {
            var message = new DeleteOrderMessage(tradeHub.code, order.id, OrderSide.BID);
            channelGroup.add(message);

            switch (order.side) {
                case BID: bidQueue.remove(order); break;
                case OFFER: offerQueue.remove(order); break;
            }
        }
    }
}
