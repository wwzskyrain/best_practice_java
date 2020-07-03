package base;

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class LongEventMain {

    private static final Logger logger = LoggerFactory.getLogger(LongEventMain.class);

    public static void main(String[] args) throws Exception {

        //创建缓冲池
        ExecutorService executor = Executors.newCachedThreadPool();
        //创建工厂
        LongEventFactory factory = new LongEventFactory();
        //创建bufferSize ,也就是RingBuffer大小，必须是2的N次方
        int ringBufferSize = 1024 * 1024; //

        /**
         //BlockingWaitStrategy 是最低效的策略，但其对CPU的消耗最小并且在各种不同部署环境中能提供更加一致的性能表现
         WaitStrategy BLOCKING_WAIT = new BlockingWaitStrategy();
         //SleepingWaitStrategy 的性能表现跟BlockingWaitStrategy差不多，对CPU的消耗也类似，但其对生产者线程的影响最小，适合用于异步日志类似的场景
         WaitStrategy SLEEPING_WAIT = new SleepingWaitStrategy();
         //YieldingWaitStrategy 的性能是最好的，适合用于低延迟的系统。在要求极高性能且事件处理线数小于CPU逻辑核心数的场景中，推荐使用此策略；例如，CPU开启超线程的特性
         WaitStrategy YIELDING_WAIT = new YieldingWaitStrategy();
         */

        //创建disruptor
        Disruptor<LongEvent> disruptor =
                new Disruptor<LongEvent>(factory,
                        ringBufferSize,
                        executor,
                        ProducerType.SINGLE,
                        new YieldingWaitStrategy());

        // 连接消费事件方法
        LongEventHandler1 handler1 = new LongEventHandler1("handler1");
        LongEventHandler2 handler2 = new LongEventHandler2("handler2");
        LongEventHandler3 handler3 = new LongEventHandler3("handler3");
        LongEventHandler4 handler4 = new LongEventHandler4("handler4");
        LongEventHandler5 handler5 = new LongEventHandler5("handler5");



        disruptor.handleEventsWith(handler1);
        disruptor.after(handler1).handleEventsWith(handler2);
        disruptor.after(handler2).handleEventsWith(handler3);
        disruptor.after(handler3).handleEventsWith(handler4);
        disruptor.after(handler4).handleEventsWith(handler5);


        // 启动
        disruptor.start();


        //Disruptor 的事件发布过程是一个两阶段提交的过程：
        //发布事件
        RingBuffer<LongEvent> ringBuffer = disruptor.getRingBuffer();

//        LongEventProducer producer = new LongEventProducer(ringBuffer);
        LongEventProducerWithTranslator producer = new LongEventProducerWithTranslator(ringBuffer);
        ByteBuffer byteBuffer = ByteBuffer.allocate(8);
        for (long l = 0; l < 10; l++) {
            byteBuffer.putLong(0, l);
            producer.onData(byteBuffer);
        }

        for (long l = 0; l < 10; l++) {
            logger.info("sleep {}", l);
            TimeUnit.SECONDS.sleep(1);

        }

        for (long l = 10; l < 20; l++) {
            byteBuffer.putLong(0, l);
            producer.onData(byteBuffer);
            TimeUnit.SECONDS.sleep(1);
        }


        disruptor.shutdown();//关闭 disruptor，方法会堵塞，直至所有的事件都得到处理；
        executor.shutdown();//关闭 disruptor 使用的线程池；如果需要的话，必须手动关闭， disruptor 在 shutdown 时不会自动关闭；

    }
}
