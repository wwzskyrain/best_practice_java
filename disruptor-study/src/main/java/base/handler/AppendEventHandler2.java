package base.handler;

import com.lmax.disruptor.EventHandler;
import event.AppendEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author erik.wang
 * @date 2020-07-04 11:26
 */
public class AppendEventHandler2 implements EventHandler<AppendEvent> {

    private static final Logger logger = LoggerFactory.getLogger(AppendEventHandler2.class);

    @Override
    public void onEvent(AppendEvent event, long sequence, boolean endOfBatch) throws Exception {
        event.handleMe(AppendEventHandler2.class);
        System.out.println(event);
//        logger.info("handle_event, with_id={}", event.getId());
    }
}
