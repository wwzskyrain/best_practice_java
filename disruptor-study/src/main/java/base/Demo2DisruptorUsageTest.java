package base;

import base.handler.*;
import com.lmax.disruptor.*;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.sun.istack.internal.NotNull;
import event.AppendEvent;
import event.LongEvent;
import event.NumberEvent;
import org.junit.Test;

import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.IntStream;

/**
 * @author erik.wang
 * Disruptor 的基本用法
 * 1ß.创建RingBuffer
 * 2.构造EventProcessor并提交给线程池
 * 3.生产者发布事件
 */
public class Demo2DisruptorUsageTest {

    public static class DisruptorUsageThreadFactory implements ThreadFactory {
        private static AtomicLong count = new AtomicLong();

        @Override
        public Thread newThread(@NotNull Runnable r) {
            return new Thread(r, DisruptorUsageThreadFactory.class.getName() + "_" + count.getAndIncrement());
        }
    }

    private static final int BUFFER_SIZE = 1 << 10;

    @Test
    public void test_basic_usage() {
        Disruptor<LongEvent> disruptor =
                new Disruptor<LongEvent>(
                        LongEvent::new,
                        BUFFER_SIZE,
                        new DisruptorUsageThreadFactory(),
                        ProducerType.SINGLE,
                        new YieldingWaitStrategy());


        LongEventHandler1 handler1 = new LongEventHandler1();
        disruptor.handleEventsWith(handler1);
        disruptor.start();
        LongEventProducerWithTranslator producer = new LongEventProducerWithTranslator(disruptor.getRingBuffer());
        try {
            producer.productEvent(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        disruptor.shutdown();
    }

    @Test
    public void test_topology() {

        Disruptor<AppendEvent> appendEventDisruptor = new Disruptor<>(
                AppendEvent::new,
                BUFFER_SIZE,
                new DisruptorUsageThreadFactory(),
                ProducerType.SINGLE,
                new YieldingWaitStrategy());
//构建拓扑-两个串行的菱形
        appendEventDisruptor
                .handleEventsWith(new AppendEventHandler1(), new AppendEventHandler2())
                .handleEventsWith(new AppendEventHandler3())
                .handleEventsWith(new AppendEventHandler4(), new AppendEventHandler5())
                .handleEventsWith(new AppendEventHandler6());

        appendEventDisruptor.start();

        EventTranslator<AppendEvent> translator = (AppendEvent event, long sequence) -> {
        };

        for (int i = 0; i < 10; i++) {
            appendEventDisruptor.publishEvent(translator);
            try {
                TimeUnit.MILLISECONDS.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        appendEventDisruptor.shutdown();
    }

    @Test
    public void test_work_handler() throws InterruptedException {

        Disruptor<AppendEvent> numberEventDisruptor = new Disruptor<>(
                AppendEvent::new,
                1 << 10,
                (Runnable r) -> new Thread(r, "thread_name_"),
                ProducerType.SINGLE,
                new SleepingWaitStrategy());


        numberEventDisruptor
                .handleEventsWithWorkerPool(new AppendEventWorkHandler1("handler1-1"), new AppendEventWorkHandler1("handler1-2"))
                .handleEventsWithWorkerPool(new AppendEventWorkHandler2("handler2-1"), new AppendEventWorkHandler2("handler2-2"))
                .handleEventsWithWorkerPool(new AppendEventWorkHandler3("handler3-1"));


        numberEventDisruptor.start();

        EventTranslator<AppendEvent> translator = (AppendEvent event, long sequence) -> {
        };

        Random random =new Random(System.currentTimeMillis());
        for (int i = 0; i < 100; i++) {
            numberEventDisruptor.publishEvent(translator);
            TimeUnit.MILLISECONDS.sleep(random.nextInt(100));
        }

        numberEventDisruptor.shutdown();
    }
}  