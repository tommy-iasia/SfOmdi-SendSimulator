package com.iasia.order;

import java.util.Comparator;

public class OfferOrderComparator implements Comparator<Order> {

    @Override
    public int compare(Order o1, Order o2) {
        var price = o1.priceRaised - o2.priceRaised;
        if (price != 0) {
            return price;
        } else {
            return Long.compare(o1.id, o2.id);
        }
    }
}
