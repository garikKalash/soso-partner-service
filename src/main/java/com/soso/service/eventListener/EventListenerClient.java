package com.soso.service.eventListener;

import com.soso.service.BaseRestClient;

import javax.validation.constraints.NotNull;

/**
 * Created by Garik Kalashyan on 5/1/2017.
 */
public class EventListenerClient extends BaseRestClient {
    public EventListenerClient(@NotNull Integer serviceId) {
        super(serviceId);
    }
}
