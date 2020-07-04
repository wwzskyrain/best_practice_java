package base.handler;

import com.lmax.disruptor.WorkHandler;
import event.NumberEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author erik.wang
 * @date 2020-07-04 22:09
 */
public class NumberEventHandler1 implements WorkHandler<NumberEvent> {

    private static final Logger logger = LoggerFactory.getLogger(NumberEventHandler1.class);

    private String name;

    public NumberEventHandler1(String name) {
        this.name = name;
    }

    @Override
    public void onEvent(NumberEvent event) throws Exception {
        logger.info("{} handle event:{}", name, event);
    }
}
