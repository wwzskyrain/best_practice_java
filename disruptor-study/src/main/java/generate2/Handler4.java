package generate2;

import com.lmax.disruptor.EventHandler;
import generate1.Trade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Handler4 implements EventHandler<Trade> {

    private static final Logger logger = LoggerFactory.getLogger(Handler4.class);

    @Override
    public void onEvent(Trade event, long sequence, boolean endOfBatch) throws Exception {
        logger.info("handler4: endOfBatch:{}, sequence:{}, event:{}", endOfBatch, sequence, event);
    }
}  