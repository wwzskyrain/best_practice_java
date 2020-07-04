package generate2;

import com.lmax.disruptor.EventHandler;
import generate1.Trade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.PrintThread;

import java.util.concurrent.TimeUnit;

public class Handler3 implements EventHandler<Trade> {

    private static final Logger logger = LoggerFactory.getLogger(Handler3.class);

    @Override
    public void onEvent(Trade event, long sequence, boolean endOfBatch) throws Exception {
        logger.info("handler3: endOfBatch:{}, sequence:{}, event:{}", endOfBatch, sequence, event);
        TimeUnit.SECONDS.sleep(1);
    }
}
