package generate2;

import com.lmax.disruptor.EventHandler;
import generate1.Trade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Handler1 implements EventHandler<Trade> {

    private static final Logger logger = LoggerFactory.getLogger(Handler1.class);


    @Override
    public void onEvent(Trade event, long sequence, boolean endOfBatch) throws Exception {
        event.setName("h1");
        logger.info("set_name:{}", event);
    }

    public void onEvent(Trade event) throws Exception {

    }
}  