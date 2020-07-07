package base.handler;

import com.lmax.disruptor.EventHandler;
import event.AppendEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author erik.wang
 * @date 2020-07-06 18:09
 */
public class AppendEventHandlerPrintId implements EventHandler<AppendEvent> {

    private static final Logger logger = LoggerFactory.getLogger(AppendEventHandlerPrintId.class);

    @Override
    public void onEvent(AppendEvent event, long sequence, boolean endOfBatch) throws Exception {
        logger.info("eventId={}", event.getId());
    }
}
