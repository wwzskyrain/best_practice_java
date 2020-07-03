package base;

import com.lmax.disruptor.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

//我们还需要一个事件消费者，也就是一个事件处理器。这个事件处理器简单地把事件中存储的数据打印到终端：
public class LongEventHandler2 implements EventHandler<LongEvent> {

    private static final Logger logger = LoggerFactory.getLogger(LongEventHandler2.class);
    private String name;

    public LongEventHandler2(String name) {
        this.name = name;
    }

    @Override
    public void onEvent(LongEvent longEvent, long l, boolean b) throws Exception {
        TimeUnit.SECONDS.sleep(1);
        logger.info("name:{} - {}", name, longEvent.getValue());
    }

}
