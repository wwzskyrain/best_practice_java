package erik.study.disruptor.handler;

import com.lmax.disruptor.WorkHandler;
import erik.study.disruptor.event.NumberEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author erik.wang
 * @date 2020-07-04 22:09
 */
public class NumberEventHandler2 implements WorkHandler<NumberEvent> {

    private static final Logger logger = LoggerFactory.getLogger(NumberEventHandler2.class);

    private String name;

    public NumberEventHandler2(String name) {
        this.name = name;
    }

    @Override
    public void onEvent(NumberEvent event) throws Exception {
        logger.info("{} handle erik.study.disruptor.event:{}", name, event);
    }
}
