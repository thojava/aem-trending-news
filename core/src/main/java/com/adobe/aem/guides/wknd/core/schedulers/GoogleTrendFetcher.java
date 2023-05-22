package com.adobe.aem.guides.wknd.core.schedulers;

import com.adobe.aem.guides.wknd.core.schedulers.config.GoogleTrendFetcherConfig;
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

@Component(service = Runnable.class, immediate = true)
@Designate(ocd = GoogleTrendFetcherConfig.class)
public class GoogleTrendFetcher implements Runnable {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Reference
    private SlingSettingsService slingSettings;
    @Reference
    private Scheduler scheduler;
    private String googleTrendHost;
    private String schedulerJobName;

    @Override
    public void run() {
        logger.debug("GoogleTrendFetcher is now running, googleTrendHost='{}'", googleTrendHost);
    }

    @Activate
    @Modified
    protected void active(GoogleTrendFetcherConfig config) {
        logger.debug("GoogleTrendFetcher is activated");
        if(isAuthor()) {
            this.schedulerJobName = this.getClass().getSimpleName();
            googleTrendHost = config.googleTrendHost();
            this.addScheduler(config);
        }
    }

    private void addScheduler(GoogleTrendFetcherConfig config) {
        if(config.enabled()) {
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
     * It is used to check whether AEM is running in Publish mode or not.
     * @return Returns true is AEM is in publish mode, false otherwise
     */
    private boolean isAuthor() {
        return this.slingSettings.getRunModes().contains("author");
    }
}
