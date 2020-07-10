package erik.study.disruptor.handler;

import com.lmax.disruptor.EventHandler;
import erik.study.disruptor.event.LongEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.concurrent.TimeUnit;

//我们还需要一个事件消费者，也就是一个事件处理器。这个事件处理器简单地把事件中存储的数据打印到终端：
public class LongEventHandler3 implements EventHandler<LongEvent> {

    private static final Logger logger = LoggerFactory.getLogger(LongEventHandler3.class);

    @Override
    public void onEvent(LongEvent longEvent, long sequence, boolean endOfBatch) throws Exception {
        TimeUnit.MILLISECONDS.sleep(new Random(System.currentTimeMillis()).nextInt(1000));
        logger.info("sequence:{}, endOfBatch:{}, longEvent:{} ", sequence, endOfBatch, longEvent);
    }

}
