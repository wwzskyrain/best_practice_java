package erik.study.disruptor.handler;

import com.lmax.disruptor.EventHandler;
import erik.study.disruptor.event.Trade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author erik.wang
 * @date 2020-07-10 18:55
 */
public class TradeHandler implements EventHandler<Trade> {

    private static final Logger logger = LoggerFactory.getLogger(TradeHandler.class);

    @Override
    public void onEvent(Trade event, long sequence, boolean endOfBatch) {
        logger.info("event:{}, sequence:{}, endOfBatch:{}", event, sequence, endOfBatch);
    }
}
