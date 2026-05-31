package com.tracker.portfolio_tracker.controller;
import org.springframework.beans.factory.annotation.Value;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/fundamentals")
@RequiredArgsConstructor
public class FundamentalsController {

    private final WebClient webClient = WebClient.create("https://finnhub.io/api/v1");

    @Value("${finnhub.api.key}")
    private String apiKey;

    @GetMapping("/{ticker}")
    public Mono<String> getFundamentals(@PathVariable String ticker) {
        return webClient.get()
                .uri(uri -> uri.path("/stock/metric")
                        .queryParam("symbol", ticker)
                        .queryParam("metric", "all")
                        .queryParam("token", apiKey)
                        .build())
                .retrieve()
                .bodyToMono(String.class);
    }

    @GetMapping("/profile/{ticker}")
    public Mono<String> getProfile(@PathVariable String ticker) {
        return webClient.get()
                .uri(uri -> uri.path("/stock/profile2")
                        .queryParam("symbol", ticker)
                        .queryParam("token", apiKey)
                        .build())
                .retrieve()
                .bodyToMono(String.class);
    }
}