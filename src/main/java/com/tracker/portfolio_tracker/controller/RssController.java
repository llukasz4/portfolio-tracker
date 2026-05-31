package com.tracker.portfolio_tracker.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/rss")
@RequiredArgsConstructor
public class RssController {

    private final WebClient webClient = WebClient.create();

    @GetMapping("/fetch")
    public Mono<String> fetchRss(@RequestParam String url) {
        return webClient.get()
                .uri(url)
                .header("User-Agent", "Mozilla/5.0")
                .retrieve()
                .bodyToMono(String.class);
    }
}