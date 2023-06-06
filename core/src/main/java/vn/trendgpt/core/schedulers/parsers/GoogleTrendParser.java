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
import vn.trendgpt.core.pojo.realtimetrend.RealtimeTrends;
import vn.trendgpt.core.pojo.realtimetrend.TrendingStory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class GoogleTrendParser {
    private static final Logger logger = LoggerFactory.getLogger(GoogleTrendParser.class);
    public static List<TrendArticle> parseStories(String contentURL) throws IOException {
        String jsonContent = IOUtils.toString(new URL(contentURL), Charset.defaultCharset());
        RealtimeTrends realtimeTrend = new Gson().fromJson(jsonContent, RealtimeTrends.class);

        List<TrendingStory> trendingStories = realtimeTrend.getStorySummaries().getTrendingStories().subList(0, 3);
        List<TrendArticle> articles = new ArrayList<>();
        for (TrendingStory trendingStory : trendingStories) {
            TrendArticle.TrendArticleBuilder articleBuilder = TrendArticle.builder();

            articleBuilder.image(trendingStory.getImage().getImgUrl());
            Article firstArticle = trendingStory.getArticles().get(0);
            logger.debug("Parsing article {}", firstArticle.getUrl());
            articleBuilder.title(firstArticle.getArticleTitle());
            try {
                articleBuilder.body(parseArticleContent(firstArticle.getUrl()));
            } catch (BoilerpipeProcessingException e) {
                logger.error(String.format("Error when parsing content from URL %s", firstArticle.getUrl()), e);
                continue;
            }
            articles.add(articleBuilder.build());
        }

        return articles;
    }

    private static String parseArticleContent(String articleURL) throws IOException, BoilerpipeProcessingException {
        Document document = Jsoup.connect(articleURL).get();
        Element body = document.getElementsByTag("body").get(0);
        return ArticleExtractor.INSTANCE.getText(body.outerHtml());
    }
}
