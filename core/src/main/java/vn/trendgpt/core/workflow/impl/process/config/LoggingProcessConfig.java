package vn.trendgpt.core.workflow.impl.process.config;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "Customized Workflow Logging Process configuration")
public @interface LoggingProcessConfig {
    @AttributeDefinition(description = "Process Label", defaultValue = "Customized Logging Process")
    String process_label();
}
