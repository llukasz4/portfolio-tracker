package com.tracker.portfolio_tracker.controller;

import com.tracker.portfolio_tracker.model.NewsArticle;
import com.tracker.portfolio_tracker.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/news")
@RequiredArgsConstructor
public class NewsController {

    private final NewsService newsService;

    @GetMapping("/{ticker}")
    public ResponseEntity<List<NewsArticle>> getNewsByTicker(@PathVariable String ticker) {
        return ResponseEntity.ok(newsService.getNewsByTicker(ticker));
    }

    @GetMapping("/unread")
    public ResponseEntity<List<NewsArticle>> getUnreadNews() {
        return ResponseEntity.ok(newsService.getUnreadNews());
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        newsService.markAsRead(id);
        return ResponseEntity.noContent().build();
    }
}