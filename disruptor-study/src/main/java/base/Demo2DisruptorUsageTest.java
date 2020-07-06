package base;

import base.handler.*;
import com.google.common.primitives.Ints;
import com.lmax.disruptor.*;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.sun.istack.internal.NotNull;
import event.AppendEvent;
import event.LongEvent;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.IntStream;

/**
 * @author erik.wang
 */
public class Demo2DisruptorUsageTest {

    private static final Logger logger = LoggerFactory.getLogger(Demo2DisruptorUsageTest.class);

    public static class DisruptorUsageThreadFactory implements ThreadFactory {
        private static AtomicLong count = new AtomicLong();

        @Override
        public Thread newThread(@NotNull Runnable r) {
            return new Thread(r, DisruptorUsageThreadFactory.class.getSimpleName() + "_" + count.getAndIncrement());
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
    public void test_multi_consumer_with_broadcast() {

        Disruptor<AppendEvent> appendEventDisruptor = new Disruptor<>(
                AppendEvent::new,
                BUFFER_SIZE,
                new DisruptorUsageThreadFactory(),
                ProducerType.SINGLE,
                new YieldingWaitStrategy());

        EventTranslatorOneArg<AppendEvent, AtomicInteger> translatorOneArg = (AppendEvent event, long sequence, AtomicInteger atomicInteger) -> {
            event.setId(atomicInteger.getAndIncrement());
        };

        appendEventDisruptor.handleEventsWith(new AppendEventHandlerPrintId(), new AppendEventHandlerPrintId(), new AppendEventHandlerPrintId());
        appendEventDisruptor.start();

        AtomicInteger atomicInteger = new AtomicInteger(1);
        IntStream.range(0, 10).forEach(id -> appendEventDisruptor.publishEvent(translatorOneArg, atomicInteger));

        sleepSomeMilliSecond(5000);
        appendEventDisruptor.shutdown();
    }

    @Test
    public void test_multi_consumer_with_round_robin() {
        Disruptor<AppendEvent> appendEventDisruptor = new Disruptor<>(
                AppendEvent::new,
                BUFFER_SIZE,
                new DisruptorUsageThreadFactory(),
                ProducerType.SINGLE,
                new YieldingWaitStrategy());
        //handler1-1,handler1-2,handler1-3分别在三个线程中，轮训着处理事件流
        appendEventDisruptor
                .handleEventsWithWorkerPool(
                        new AppendEventWorkHandler1("handler1-1"),
                        new AppendEventWorkHandler1("handler1-2"),
                        new AppendEventWorkHandler1("handler1-3"));

        appendEventDisruptor.start();
        buildTranslatorAndPublishEvent(appendEventDisruptor);
        appendEventDisruptor.shutdown();
    }

    /**
     * 阻塞生产者
     */
    @Test
    public void test_block_product() {

        RingBuffer<AppendEvent> ringBuffer = RingBuffer.create(
                ProducerType.SINGLE,
                AppendEvent::new,
                1 << 3,
                new SleepingWaitStrategy());

        ExecutorService executor = Executors.newCachedThreadPool();
        BatchEventProcessor<AppendEvent> processor1 = new BatchEventProcessor<>(ringBuffer, ringBuffer.newBarrier(), new AppendEventHandlerWithHeavyConsume(3));
        ringBuffer.addGatingSequences(processor1.getSequence());

        executor.submit(processor1);

        IntStream.range(0, 100).forEach(id -> {
            //当生产者大于消费者一圈时，next()会阻塞
            long sequence = ringBuffer.next();
            logger.info("success_acquire_sequence:{}", sequence);
            try {
                AppendEvent appendEvent = ringBuffer.get(sequence);
                appendEvent.setId(((int) sequence));
            } finally {
                ringBuffer.publish(sequence);
            }
            sleepSomeMilliSecond(1000);
        });
    }

    /**
     * 这会表现为'handler1'处理完成了所有的事件之后，才一起交给handler2，即handler2被handler1阻塞了。
     * 其实这是BatchEventProcessor的一个优化假象，可以让handler实现BatchStartAware接口来验证，除此之外，
     * 还可以用一下两种方法消除这个假象
     * 1.扩大测试规模
     * 2.降低生产速率
     */
    @Test
    public void test_batch_consume() {

        Disruptor<AppendEvent> appendEventDisruptor = new Disruptor<>(
                AppendEvent::new,
                BUFFER_SIZE,
                new DisruptorUsageThreadFactory(),
                ProducerType.SINGLE,
                new YieldingWaitStrategy());

        appendEventDisruptor
                .handleEventsWith(new AppendEventHandler1())
                .handleEventsWith(new AppendEventHandler2())
                .handleEventsWith(new AppendEventHandler3());

        appendEventDisruptor.start();

        EventTranslatorOneArg<AppendEvent, Integer> translator =
                (AppendEvent event, long sequence, Integer arg0) -> event.setId(arg0);

        IntStream.range(0, 10).forEach(id ->
                appendEventDisruptor.publishEvent(translator, id));

        appendEventDisruptor.shutdown();
        System.out.println("test_over.");
    }


    /**
     * 多生产者模式
     */
    @Test
    public void test_multi_produce() {
        Disruptor<AppendEvent> multiProductDisruptor = new Disruptor<>(
                AppendEvent::new,
                BUFFER_SIZE,
                new DisruptorUsageThreadFactory(),
                ProducerType.MULTI,
                new YieldingWaitStrategy());

        multiProductDisruptor.handleEventsWith(new AppendEventHandlerPrintId());

        multiProductDisruptor.start();

        ExecutorService executor = Executors.newFixedThreadPool(3);
        executor.execute(new AppendEventProducer(multiProductDisruptor, new AtomicInteger(0)));
        executor.execute(new AppendEventProducer(multiProductDisruptor, new AtomicInteger(1)));
        executor.execute(new AppendEventProducer(multiProductDisruptor, new AtomicInteger(2)));

        sleepSomeMilliSecond(5000);
        executor.shutdown();
        multiProductDisruptor.shutdown();
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
        IntStream.range(0, 10).forEach(id -> appendEventDisruptor.publishEvent(translator));
        appendEventDisruptor.shutdown();
    }

    /**
     * 多线程处理handler
     *
     * @throws InterruptedException
     */
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

        Random random = new Random(System.currentTimeMillis());
        for (int i = 0; i < 100; i++) {
            numberEventDisruptor.publishEvent(translator);
            TimeUnit.MILLISECONDS.sleep(random.nextInt(100));
        }
        numberEventDisruptor.shutdown();
    }

    private void buildTranslatorAndPublishEvent(Disruptor disruptor) {

        EventTranslatorOneArg<AppendEvent, AtomicInteger> translatorOneArg =
                (AppendEvent event, long sequence, AtomicInteger atomicInteger) -> {
                    event.setId(atomicInteger.getAndIncrement());
                };

        AtomicInteger atomicInteger = new AtomicInteger(1);
        IntStream.range(0, 1000)
                .forEach(id -> disruptor.publishEvent(translatorOneArg, atomicInteger));
    }

    private void sleepSomeMilliSecond(int milli) {
        try {
            TimeUnit.MILLISECONDS.sleep(milli);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static class AppendEventProducer extends Thread {

        private static final int SPAN = 3;

        private Disruptor disruptor;
        private AtomicInteger atomicInteger;
        EventTranslatorOneArg<AppendEvent, Integer> translator = (AppendEvent event, long sequence, Integer arg0) -> event.setId(arg0);

        public AppendEventProducer(Disruptor disruptor, AtomicInteger atomicInteger) {
            this.disruptor = disruptor;
            this.atomicInteger = atomicInteger;
        }

        @Override
        public void run() {
            IntStream.range(0, 100).forEach(id ->
                    disruptor.publishEvent(translator, atomicInteger.getAndAdd(SPAN)));
        }
    }
}