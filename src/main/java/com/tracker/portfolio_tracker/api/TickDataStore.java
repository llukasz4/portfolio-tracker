package com.tracker.portfolio_tracker.api;

import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TickDataStore {

    public static class Tick {
        public final long timestamp;
        public final BigDecimal price;

        public Tick(long timestamp, BigDecimal price) {
            this.timestamp = timestamp;
            this.price = price;
        }
    }

    private final Map<String, List<Tick>> tickData = new ConcurrentHashMap<>();

    public void addTick(String ticker, BigDecimal price) {
        tickData.computeIfAbsent(ticker, k -> Collections.synchronizedList(new ArrayList<>()))
                .add(new Tick(Instant.now().getEpochSecond(), price));
        cleanup(ticker);
    }

    private void cleanup(String ticker) {
        long cutoff = Instant.now().getEpochSecond() - 3600;
        List<Tick> ticks = tickData.get(ticker);
        if (ticks != null) {
            ticks.removeIf(t -> t.timestamp < cutoff);
        }
    }

    public List<Tick> getTicks(String ticker) {
        return tickData.getOrDefault(ticker, Collections.emptyList());
    }
}