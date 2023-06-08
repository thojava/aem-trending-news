package vn.trendgpt.core.schedulers;

import com.day.cq.replication.Replicator;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.commons.scheduler.Scheduler;
import org.apache.sling.settings.SlingSettingsService;
import org.apache.sling.testing.mock.sling.services.MockSlingSettingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import vn.trendgpt.core.pojo.TrendArticle;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
public class GoogleTrendFetcherTest {
    private final AemContext ctx = new AemContext();
    @Mock
    private Replicator replicator;
    @Mock
    private Scheduler scheduler;

    private GoogleTrendFetcher serviceInstance;

    @BeforeEach
    public void setup() {
        ctx.registerService(Replicator.class, replicator);
        ctx.registerService(Scheduler.class, scheduler);
        ctx.load().json("/vn/trendgpt/core/models/impl/BylineImplTest.json", "/content");


        MockSlingSettingService slingSettingService = (MockSlingSettingService) ctx.getService(SlingSettingsService.class);
        assert slingSettingService != null;
        slingSettingService.setRunModes(Collections.singleton("author"));

        Map<String, Object> properties = new HashMap<>();
        properties.put("googleTrendHost", "https://trends.google.com/trends/api/dailytrends?hl=en-US&tz=-420&geo=VN&hl=en-US&ns=15");
        properties.put("rootContentPath", "/content");
        properties.put("enabled", false);
        serviceInstance = ctx.registerInjectActivateService(new GoogleTrendFetcher(), properties);
    }
    @Test
    void run() {
        List<TrendArticle> articles = serviceInstance.parseContent();
        assertFalse(articles.isEmpty());
    }
}

