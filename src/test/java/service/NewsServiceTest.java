package com.tracker.portfolio_tracker.service;

import com.tracker.portfolio_tracker.model.NewsArticle;
import com.tracker.portfolio_tracker.repository.NewsArticleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NewsServiceTest {

    @Mock
    private NewsArticleRepository newsArticleRepository;

    @InjectMocks
    private NewsService newsService;

    private NewsArticle article;

    @BeforeEach
    void setUp() {
        article = new NewsArticle();
        article.setId(1L);
        article.setTicker("AAPL");
        article.setHeadline("Apple hits record high");
        article.setUrl("https://example.com/news/1");
        article.setSource("Yahoo");
        article.setRead(false);
        article.setPublishedAt(LocalDateTime.now());
        article.setSentiment(NewsArticle.Sentiment.BULLISH);
    }

    @Test
    void shouldReturnNewsByTicker() {
        when(newsArticleRepository.findByTickerOrderByPublishedAtDesc("AAPL"))
                .thenReturn(List.of(article));

        List<NewsArticle> result = newsService.getNewsByTicker("AAPL");

        assertEquals(1, result.size());
        assertEquals("AAPL", result.get(0).getTicker());
        verify(newsArticleRepository, times(1)).findByTickerOrderByPublishedAtDesc("AAPL");
    }

    @Test
    void shouldReturnUnreadNews() {
        when(newsArticleRepository.findByReadFalseOrderByPublishedAtDesc())
                .thenReturn(List.of(article));

        List<NewsArticle> result = newsService.getUnreadNews();

        assertEquals(1, result.size());
        assertFalse(result.get(0).isRead());
    }

    @Test
    void shouldMarkArticleAsRead() {
        when(newsArticleRepository.findById(1L)).thenReturn(Optional.of(article));

        newsService.markAsRead(1L);

        assertTrue(article.isRead());
        verify(newsArticleRepository, times(1)).save(article);
    }

    @Test
    void shouldNotSaveDuplicateNews() {
        when(newsArticleRepository.existsByUrl(article.getUrl())).thenReturn(true);

        newsService.saveIfNotExists(article);

        verify(newsArticleRepository, never()).save(article);
    }

    @Test
    void shouldSaveNewArticle() {
        when(newsArticleRepository.existsByUrl(article.getUrl())).thenReturn(false);

        newsService.saveIfNotExists(article);

        verify(newsArticleRepository, times(1)).save(article);
    }
}