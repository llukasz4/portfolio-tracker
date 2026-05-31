package com.tracker.portfolio_tracker.repository;

import com.tracker.portfolio_tracker.model.NewsArticle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NewsArticleRepository extends JpaRepository<NewsArticle, Long> {
    List<NewsArticle> findByTickerOrderByPublishedAtDesc(String ticker);
    List<NewsArticle> findByReadFalseOrderByPublishedAtDesc();
    boolean existsByUrl(String url);
}