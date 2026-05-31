package com.tracker.portfolio_tracker.service;

import com.tracker.portfolio_tracker.model.NewsArticle;
import com.tracker.portfolio_tracker.repository.NewsArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NewsService {

    private final NewsArticleRepository newsArticleRepository;

    public List<NewsArticle> getNewsByTicker(String ticker) {
        return newsArticleRepository.findByTickerOrderByPublishedAtDesc(ticker);
    }

    public List<NewsArticle> getUnreadNews() {
        return newsArticleRepository.findByReadFalseOrderByPublishedAtDesc();
    }

    public void markAsRead(Long id) {
        newsArticleRepository.findById(id).ifPresent(article -> {
            article.setRead(true);
            newsArticleRepository.save(article);
        });
    }

    public void saveIfNotExists(NewsArticle article) {
        if (article.getHeadline() == null) return;
        if (!newsArticleRepository.existsByUrl(article.getUrl())) {
            newsArticleRepository.save(article);
        }
    }
}