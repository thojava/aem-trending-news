package vn.trendgpt.core.schedulers.config;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "Configuration is used to setup ChatGPT service")
public @interface ChatGPTConfig {
    @AttributeDefinition(name = "OpenAI Secret Key")
    String openAISecretKey();
}
