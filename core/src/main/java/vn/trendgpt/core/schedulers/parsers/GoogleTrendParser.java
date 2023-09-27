package vn.trendgpt.core.schedulers.parsers;

import com.google.gson.Gson;
import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.extractors.ArticleExtractor;
import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vn.trendgpt.core.pojo.TrendArticle;
import vn.trendgpt.core.pojo.realtimetrend.Article;
import vn.trendgpt.core.pojo.realtimetrend.DailyTrends;
import vn.trendgpt.core.pojo.realtimetrend.TrendingSearch;
import vn.trendgpt.core.pojo.realtimetrend.TrendingSearchesDay;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class GoogleTrendParser {
    private static final Logger logger = LoggerFactory.getLogger(GoogleTrendParser.class);

    public static List<TrendArticle> parseStories(String contentURL) throws IOException {
        String jsonContent = IOUtils.toString(new URL(contentURL), Charset.defaultCharset());
        jsonContent = jsonContent.replaceFirst("\\)]}',\n", "");
        DailyTrends dailyTrends = new Gson().fromJson(jsonContent, DailyTrends.class);

        List<TrendArticle> articles = new ArrayList<>();
        for (TrendingSearchesDay trendingSearchesDay : dailyTrends.getDefault_().getTrendingSearchesDays()) {
            TrendArticle.TrendArticleBuilder articleBuilder = TrendArticle.builder();

            List<TrendingSearch> trendingSearches = trendingSearchesDay.getTrendingSearches();
            for (TrendingSearch trendingSearch : trendingSearches) {
                if (isHotTrend(trendingSearch)) {
                    Article firstArticle = trendingSearch.getArticles().get(0);
                    logger.debug("Parsing article {}", firstArticle.getUrl());
                    articleBuilder.title(firstArticle.getTitle());
                    articleBuilder.image(firstArticle.getImage().getImageUrl());
                    try {
                        articleBuilder.body(parseArticleContent(firstArticle.getUrl()));
                    } catch (BoilerpipeProcessingException e) {
                        logger.error(String.format("Error when parsing content from URL %s", firstArticle.getUrl()), e);
                        continue;
                    }
                    articles.add(articleBuilder.build());
                }
            }
        }

        return articles;
    }

    private static boolean isHotTrend(TrendingSearch trendingSearch) {
        return Integer.parseInt(trendingSearch.getFormattedTraffic().replaceAll(Pattern.quote("K+"), "")) >= 10;
    }

    private static String parseArticleContent(String articleURL) throws IOException, BoilerpipeProcessingException {
        Document document = Jsoup.connect(articleURL).get();
        Element body = document.getElementsByTag("body").get(0);
        return ArticleExtractor.INSTANCE.getText(body.outerHtml());
    }
}
