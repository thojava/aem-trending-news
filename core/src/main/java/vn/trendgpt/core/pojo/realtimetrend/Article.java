package vn.trendgpt.core.pojo.realtimetrend;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Article {
    private String title;
    private String url;
    private String snippet;
    private Image image;
}
