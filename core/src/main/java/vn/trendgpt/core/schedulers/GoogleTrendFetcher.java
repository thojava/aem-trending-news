package vn.trendgpt.core.schedulers;

import com.day.cq.commons.jcr.JcrUtil;
import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.ReplicationException;
import com.day.cq.replication.Replicator;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMException;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.commons.scheduler.ScheduleOptions;
import org.apache.sling.commons.scheduler.Scheduler;
import org.apache.sling.settings.SlingSettingsService;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vn.trendgpt.core.pojo.TrendArticle;
import vn.trendgpt.core.schedulers.config.GoogleTrendFetcherConfig;
import vn.trendgpt.core.schedulers.parsers.GoogleTrendParser;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component(service = Runnable.class, immediate = true)
@Designate(ocd = GoogleTrendFetcherConfig.class)
public class GoogleTrendFetcher implements Runnable {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private static final String PAGE_TEMPLATE_PATH = "/conf/trendgpt/settings/wcm/templates/article-page";
    @Reference
    private SlingSettingsService slingSettings;
    @Reference
    private ResourceResolverFactory resourceResolverFactory;
    @Reference
    private Replicator replicator;
    @Reference
    private Scheduler scheduler;
    private String googleTrendHost;
    private String schedulerJobName;
    private String rootContentPath;

    @Override
    public void run() {
        logger.debug("GoogleTrendFetcher is now running, googleTrendHost='{}'", googleTrendHost);
        final Map<String, Object> param = new HashMap<>();
        param.put(ResourceResolverFactory.SUBSERVICE, "getResourceResolver");
        try (ResourceResolver resourceResolver = resourceResolverFactory.getServiceResourceResolver(param)) {
            List<TrendArticle> articles = parseContent();
            for (TrendArticle article : articles) {
                createPage(resourceResolver, article);
            }
        } catch (LoginException e) {
            logger.error("Error when obtaining ResourceResolver", e);
            throw new RuntimeException(e);
        }
    }

    public List<TrendArticle> parseContent() {
        try {
            return GoogleTrendParser.parseStories(googleTrendHost);
        } catch (IOException e) {
            logger.error("Error when parsing content", e);
            throw new RuntimeException(e);
        }
    }

    private void createPage(ResourceResolver resourceResolver, TrendArticle trendArticle) {
        PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
        assert pageManager != null;
        String normalizeTitle = normalizeTitle(trendArticle.getTitle());
        String pageName = createPageName(normalizeTitle);
        boolean pageNotImported = pageManager.getPage(rootContentPath + "/" + pageName) == null;
        if (pageNotImported) {
            Page createdPage;
            try {
                createdPage = pageManager.create(rootContentPath, pageName, PAGE_TEMPLATE_PATH, normalizeTitle);
            } catch (WCMException e) {
                logger.error(String.format("Error when creating new page %s", trendArticle.getTitle()), e);
                throw new RuntimeException(e);
            }
            try {
                updatePage(createdPage, trendArticle, resourceResolver);
            } catch (RepositoryException | PersistenceException e) {
                logger.error(String.format("Error when updating content to page %s", trendArticle.getTitle()), e);
                throw new RuntimeException(e);
            }
            try {
                replicator.replicate(resourceResolver.adaptTo(Session.class), ReplicationActionType.ACTIVATE, createdPage.getPath());
            } catch (ReplicationException e) {
                logger.error(String.format("Error when publishing page %s", trendArticle.getTitle()), e);
                throw new RuntimeException(e);
            }
        }
    }

    private static String createPageName(String normalizeTitle) {
        return JcrUtil.createValidName(normalizeTitle, JcrUtil.HYPHEN_LABEL_CHAR_MAPPING).replaceAll("--|---", "-");
    }

    private static String normalizeTitle(String title) {
        return StringUtils.stripAccents(title).replaceAll("đ", "d").replaceAll("Đ", "D");
    }

    private void updatePage(Page page, TrendArticle trendArticle, ResourceResolver resourceResolver) throws RepositoryException, PersistenceException {
        Node contentNode = page.getContentResource().adaptTo(Node.class);
        assert contentNode != null;
        contentNode.setProperty("jcr:title", trendArticle.getTitle());

        Node titleNode = contentNode.getNode("root/container/container/title");
        titleNode.setProperty("jcr:title", trendArticle.getTitle());

        Node bodyTextNode = contentNode.getNode("root/container/container/text");
        bodyTextNode.setProperty("text", trendArticle.getBody());
        bodyTextNode.setProperty("textIsRich", "true");

        resourceResolver.commit();
    }

    @Activate
    @Modified
    protected void active(GoogleTrendFetcherConfig config) {
        logger.debug("GoogleTrendFetcher is activated");

        this.schedulerJobName = this.getClass().getSimpleName();
        this.googleTrendHost = config.googleTrendHost();
        this.rootContentPath = config.rootContentPath();

        if (isAuthor()) {
            this.addScheduler(config);
        }
    }

    private void addScheduler(GoogleTrendFetcherConfig config) {
        if (config.enabled()) {
            ScheduleOptions scheduleOptions = scheduler.EXPR(config.schedulerExpression());
            scheduleOptions.name(schedulerJobName);

            scheduler.schedule(this, scheduleOptions);
            logger.info("{} Scheduler added", schedulerJobName);
        } else {
            removeScheduler();
        }
    }

    private void removeScheduler() {
        logger.info("Removing scheduler: {}", schedulerJobName);
        scheduler.unschedule(schedulerJobName);
    }

    /**
     * It is used to check whether AEM is running in author mode or not.
     *
     * @return Returns true is AEM is in author mode, false otherwise
     */
    private boolean isAuthor() {
        return this.slingSettings.getRunModes().contains("author");
    }
}
