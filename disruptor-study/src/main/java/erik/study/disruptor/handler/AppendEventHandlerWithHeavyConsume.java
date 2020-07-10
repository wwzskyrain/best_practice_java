package erik.study.disruptor.handler;

import com.lmax.disruptor.BatchStartAware;
import com.lmax.disruptor.EventHandler;
import erik.study.disruptor.event.AppendEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * @author erik.wang
 * @date 2020-07-06 14:33
 */
public class AppendEventHandlerWithHeavyConsume implements EventHandler<AppendEvent>, BatchStartAware {

    private static final Logger logger = LoggerFactory.getLogger(AppendEventHandlerWithHeavyConsume.class);

    private int timeout;


    @Override
    public void onBatchStart(long batchSize) {
        logger.info("batchSize:{}", batchSize);
    }

    public AppendEventHandlerWithHeavyConsume(int timeout) {
        this.timeout = timeout;
    }

    @Override
    public void onEvent(AppendEvent event, long sequence, boolean endOfBatch) throws Exception {
        logger.info("sequence:{},消费中...", event.getId());
        TimeUnit.SECONDS.sleep(timeout);
        logger.info("sequence:{},消费完成...", event.getId());
    }
}
