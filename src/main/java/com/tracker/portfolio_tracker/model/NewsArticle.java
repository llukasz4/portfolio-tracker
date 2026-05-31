package com.tracker.portfolio_tracker.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "news_articles")
@Getter @Setter @NoArgsConstructor
public class NewsArticle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String ticker;

    @Column(columnDefinition = "TEXT")
    private String headline;

    @Column(columnDefinition = "TEXT")
    private String summary;

    @Column(columnDefinition = "TEXT")
    private String url;

    private String source;

    private LocalDateTime publishedAt;
    private boolean read = false;

    @Enumerated(EnumType.STRING)
    private Sentiment sentiment;

    // ← enum wewnątrz klasy
    public enum Sentiment {
        BULLISH("We are so back"),
        BEARISH("Bears are now legally in control"),
        NEUTRAL("Waiting room energy");

        private final String mojaNazwa;

        Sentiment(String mojaNazwa) {
            this.mojaNazwa = mojaNazwa;
        }

        public String getmojaNazwa() {
            return mojaNazwa;
        }
    }
}