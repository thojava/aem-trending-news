package vn.trendgpt.core.pojo.realtimetrend;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
public class TrendingStory {
    private Image image;
    private List<Article> articles;
}
