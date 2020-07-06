package base.handler;

import com.lmax.disruptor.EventHandler;
import event.AppendEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * @author erik.wang
 * @date 2020-07-04 11:26
 */
public class AppendEventHandler1 implements EventHandler<AppendEvent> {

    private static final Logger logger = LoggerFactory.getLogger(AppendEventHandler1.class);

    @Override
    public void onEvent(AppendEvent event, long sequence, boolean endOfBatch) throws Exception {
        event.handleMe(AppendEventHandler1.class);
//        logger.info("handle_event, with_id={}", event.getId());
        TimeUnit.SECONDS.sleep(1);
        System.out.println(event);
    }
}
