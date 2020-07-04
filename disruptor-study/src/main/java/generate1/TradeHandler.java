package generate1;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.WorkHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class TradeHandler implements EventHandler<Trade>, WorkHandler<Trade> {

    private static final Logger logger = LoggerFactory.getLogger(TradeHandler.class);

    @Override
    public void onEvent(Trade event, long sequence, boolean endOfBatch) throws Exception {
        this.onEvent(event);
    }

    @Override
    public void onEvent(Trade event) throws Exception {

        String eventId = UUID.randomUUID().toString();
        event.setId(eventId);
        logger.info("");

    }
}  