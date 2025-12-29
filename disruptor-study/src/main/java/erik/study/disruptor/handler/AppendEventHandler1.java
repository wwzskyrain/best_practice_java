package erik.study.disruptor.handler;

import com.lmax.disruptor.BatchStartAware;
import com.lmax.disruptor.EventHandler;
import erik.study.disruptor.event.AppendEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author erik.wang
 * @date 2020-07-04 11:26
 */
public class AppendEventHandler1 implements EventHandler<AppendEvent>, BatchStartAware {

    private static final Logger logger = LoggerFactory.getLogger(AppendEventHandler1.class);


    @Override
    public void onBatchStart(long batchSize) {
        logger.warn("batchSize={}", batchSize);
    }

    @Override
    public void onEvent(AppendEvent event, long sequence, boolean endOfBatch) throws Exception {
        event.handleMe(AppendEventHandler1.class);
//        logger.info("handle_event, with_id={}", erik.study.disruptor.event.getId());
//        Random r = new Random();
//        TimeUnit.MILLISECONDS.sleep(r.nextInt(10));
        System.out.println(event);
    }
}
