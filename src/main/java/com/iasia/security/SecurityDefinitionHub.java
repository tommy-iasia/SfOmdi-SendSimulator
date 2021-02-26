package com.iasia.security;

import com.iasia.hub.SingleHub;
import com.iasia.net.ChannelGroup;
import com.iasia.net.Message;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class SecurityDefinitionHub extends SingleHub {

    public SecurityDefinitionHub(ChannelGroup channelGroup) {
        super(channelGroup);
    }

    public final static List<SecurityDefinitionMessage> securities = Arrays.asList(
            new SecurityDefinitionMessage(1, "MAIN", "CKH Holdings", "HKD"),
            new SecurityDefinitionMessage(2, "MAIN", "CLP Holdings", "HKD"),
            new SecurityDefinitionMessage(3, "MAIN", "Hong Kong and China Gas Co.Ltd.", "HKD"),
            new SecurityDefinitionMessage(4, "MAIN", "Wharf Holdings", "HKD"),
            new SecurityDefinitionMessage(5, "MAIN", "HSBC Holdings", "HKD"));

    private Queue<SecurityDefinitionMessage> messages = new LinkedList<>(securities);
    @Override
    protected Message next() {
        return messages.poll();
    }
}
