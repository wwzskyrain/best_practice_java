package generate2;

import com.lmax.disruptor.EventHandler;
import generate1.Trade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Handler2 implements EventHandler<Trade> {

    private static final Logger logger = LoggerFactory.getLogger(Handler2.class);

    @Override
    public void onEvent(Trade event, long sequence, boolean endOfBatch) throws Exception {
        event.setPrice(17.0);
        logger.info("set_price:{}", event);
    }

}  