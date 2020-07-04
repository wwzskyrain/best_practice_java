package base;

import com.lmax.disruptor.*;
import com.lmax.disruptor.dsl.ProducerType;
import event.LongEvent;
import event.LongEventFactory;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;

/**
 * @author erik.wang
 * @date 2020-07-03 13:42
 */
public class WorkHandlerTest {

    @Test
    public void test_() {
        RingBuffer<LongEvent> buffer = RingBuffer.create(ProducerType.SINGLE, new LongEventFactory(), 1 << 2, new BlockingWaitStrategy());
        WorkHandler<LongEvent> workHandler = new WorkHandler<LongEvent>() {

            private final Logger logger = LoggerFactory.getLogger(WorkHandlerTest.class);
            @Override
            public void onEvent(LongEvent event) throws Exception {
                logger.info("event:{}", event);
            }
        };

        WorkerPool<LongEvent> workerPool = new WorkerPool<LongEvent>(buffer, buffer.newBarrier(), new IgnoreExceptionHandler(), workHandler, workHandler);
        Sequence[] sequences = workerPool.getWorkerSequences();
        buffer.addGatingSequences(sequences);
        workerPool.start(Executors.newFixedThreadPool(10));


        System.out.println("开始生产");
        for (int i = 0; i < 10; i++) {
            long next = buffer.next();
            try {
                LongEvent event = buffer.get(next);
                event.setValue(i);
            } finally {
                System.out.println("生产:" + i);
                buffer.publish(next);
            }
        }
    }

}
