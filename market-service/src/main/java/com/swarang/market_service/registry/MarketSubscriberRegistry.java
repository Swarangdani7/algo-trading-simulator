package com.swarang.market_service.registry;

import com.swarang.market.grpc.MarketPrice;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class MarketSubscriberRegistry {

    private final Map<String, StreamObserver<MarketPrice>> subscribers = new ConcurrentHashMap<>();

    public void add(String runId, StreamObserver<MarketPrice> observer){
        log.info("Subscribed to market server [subId = {}]", runId);
        subscribers.put(runId, observer);
    }

    public void remove(String runId){
        log.info("Unsubscribed from market server [subId = {}]", runId);
        StreamObserver<MarketPrice> observer = subscribers.remove(runId);
        if(observer != null){
            try{ observer.onCompleted(); }
            catch (Exception ignored) {}
        }
    }

    public Collection<StreamObserver<MarketPrice>> fetchAllSubscribers(){
        return new ArrayList<>(subscribers.values());
    }

    public void removeObserver(StreamObserver<MarketPrice> observer){
        subscribers.values().removeIf(o -> o == observer);
    }
}
