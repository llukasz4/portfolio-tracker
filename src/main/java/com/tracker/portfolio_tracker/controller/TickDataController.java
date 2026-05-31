package com.tracker.portfolio_tracker.controller;

import com.tracker.portfolio_tracker.api.TickDataStore;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/ticks")
@RequiredArgsConstructor
public class TickDataController {

    private final TickDataStore tickDataStore;

    @GetMapping("/{ticker}")
    public List<Map<String, Object>> getTicks(
            @PathVariable String ticker,
            @RequestParam(defaultValue = "60") int seconds) {

        long cutoff = System.currentTimeMillis() / 1000 - seconds;

        return tickDataStore.getTicks(ticker)
                .stream()
                .filter(t -> t.timestamp >= cutoff)
                .map(t -> {
                    Map<String, Object> map = new java.util.HashMap<>();
                    map.put("time", t.timestamp);
                    map.put("value", t.price);
                    return map;
                })
                .collect(Collectors.toList());
    }
}