package vn.trendgpt.core.pojo.realtimetrend;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TrendingSearch {
    private String formattedTraffic;
    private List<Article> articles;
}
