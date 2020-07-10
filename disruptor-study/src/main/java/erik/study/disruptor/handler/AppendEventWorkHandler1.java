package erik.study.disruptor.handler;

import com.lmax.disruptor.WorkHandler;
import erik.study.disruptor.event.AppendEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author erik.wang
 * @date 2020-07-04 22:19
 */
public class AppendEventWorkHandler1 implements WorkHandler<AppendEvent> {

    private static final Logger logger = LoggerFactory.getLogger(AppendEventWorkHandler1.class);

    private String name;

    public AppendEventWorkHandler1(String name) {
        this.name = name;
    }

    @Override
    public void onEvent(AppendEvent event) throws Exception {
        event.handleMe(name);
        logger.info("{}", event);
    }
}
