package erik.study.disruptor.handler;

import com.lmax.disruptor.EventHandler;
import erik.study.disruptor.event.LongEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class LongEventHandler4 implements EventHandler<LongEvent> {

    private static final Logger logger = LoggerFactory.getLogger(LongEventHandler4.class);

    @Override
    public void onEvent(LongEvent longEvent, long sequence, boolean endOfBatch) throws Exception {
        TimeUnit.MILLISECONDS.sleep(new Random(System.currentTimeMillis()).nextInt(1000));
        logger.info("sequence:{}, endOfBatch:{}, longEvent:{} ", sequence, endOfBatch, longEvent);
    }

}
