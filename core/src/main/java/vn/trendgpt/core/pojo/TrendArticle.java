package vn.trendgpt.core.pojo;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class TrendArticle {
    private String title;
    private String image;
    private String body;
}
