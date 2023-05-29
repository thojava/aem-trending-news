package vn.trendgpt.core.workflow.impl.process;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vn.trendgpt.core.workflow.impl.process.config.LoggingProcessConfig;

@Component(service = WorkflowProcess.class)
@Designate(ocd = LoggingProcessConfig.class)
public class LoggingProcess implements WorkflowProcess {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Override
    public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap metaDataMap) throws WorkflowException {
        logger.debug("Process {} is executing", getClass().getSimpleName());
    }
}
