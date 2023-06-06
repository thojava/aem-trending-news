package vn.trendgpt.core.schedulers.config;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name="A scheduled task to fetch google trend news")
public @interface GoogleTrendFetcherConfig {
    @AttributeDefinition(name = "Cron-job expression")
    String schedulerExpression() default "*/30 * * * * ?";

    @AttributeDefinition(name = "Enabled", description = "True, if scheduler service is enabled", type = AttributeType.BOOLEAN)
    boolean enabled() default true;

    @AttributeDefinition(name = "Google Trend Host", description = "This is where system fetch google trend data", type = AttributeType.STRING)
    String googleTrendHost();

    @AttributeDefinition(name ="Root Content Path", description = "The folder path where fetcher will saved imported articles", type = AttributeType.STRING)
    String rootContentPath() default "/content";
}
