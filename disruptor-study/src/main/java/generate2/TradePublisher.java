package generate2;

import com.lmax.disruptor.EventTranslator;
import com.lmax.disruptor.dsl.Disruptor;
import generate1.Trade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author
 */
public class TradePublisher implements Runnable {

    public static final Logger logger = LoggerFactory.getLogger(TradePublisher.class);
    public static AtomicLong count = new AtomicLong();

    Disruptor<Trade> disruptor;
    private CountDownLatch latch;

    private static int LOOP = 10;

    public TradePublisher(CountDownLatch latch, Disruptor<Trade> disruptor) {
        this.disruptor = disruptor;
        this.latch = latch;
    }

    @Override
    public void run() {
        TradeEventTranslator tradeTranslator = new TradeEventTranslator();
        for (int i = 0; i < LOOP; i++) {
            disruptor.publishEvent(tradeTranslator);
            logger.info("success_publish_event.");
        }
        latch.countDown();
    }

}

class TradeEventTranslator implements EventTranslator<Trade> {

    @Override
    public void translateTo(Trade event, long sequence) {
        this.generateTrade();
    }

    private Trade generateTrade() {

        Trade trade = new Trade();
        trade.setId(String.valueOf(TradePublisher.count.getAndAdd(1)));
        return trade;
    }

}  