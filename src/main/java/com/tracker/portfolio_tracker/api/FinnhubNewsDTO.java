package com.tracker.portfolio_tracker.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class FinnhubNewsDTO {

    private String headline;
    private String source;
    private String url;
    private String summary;

    @JsonProperty("related")
    private String ticker;

    @JsonProperty("datetime")
    private long publishedTimestamp;
}