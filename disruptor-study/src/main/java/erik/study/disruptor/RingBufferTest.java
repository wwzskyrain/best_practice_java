package erik.study.disruptor;

import com.lmax.disruptor.BatchEventProcessor;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.SequenceBarrier;
import com.lmax.disruptor.YieldingWaitStrategy;
import erik.study.disruptor.event.Trade;
import erik.study.disruptor.handler.TradeHandler;
import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author erik.wang
 * RingBuffer的基本用法
 * 1.创建RingBuffer
 * 2.构造EventProcessor并提交给线程池
 * 3.生产者发布事件
 * 4.完事后关闭线程池
 */
public class RingBufferTest {

    @Test
    public void test_ring_buffer_basic_usage() throws Exception {

        int bufferSize = 1024;
        int threadNumber = 4;
        int eventNumber = 10;
        /*
         * createSingleProducer创建一个单生产者的RingBuffer，
         * 第一个参数叫EventFactory，从名字上理解就是"事件工厂"，其实它的职责就是产生数据填充RingBuffer的区块。
         * 第二个参数是RingBuffer的大小，它必须是2的指数 目的是为了将求模运算转为&运算提高效率
         * 第三个参数是RingBuffer的生产都在没有可用区块的时候(可能是消费者（或者说是事件处理器） 太慢了)的等待策略
         */
        final RingBuffer<Trade> ringBuffer = RingBuffer.createSingleProducer(
                Trade::new,
                bufferSize,
                new YieldingWaitStrategy());

        ExecutorService executors = Executors.newFixedThreadPool(threadNumber);

        //创建SequenceBarrier  
        SequenceBarrier sequenceBarrier = ringBuffer.newBarrier();

        //创建消息处理器  
        BatchEventProcessor<Trade> tradeProcessor = new BatchEventProcessor<>(
                ringBuffer, sequenceBarrier, new TradeHandler());

        //把消费者的位置信息引用注入到生产者，在关闭时Disruptor时需要用到
        ringBuffer.addGatingSequences(tradeProcessor.getSequence());

        //把消息处理器提交到线程池  
        executors.submit(tradeProcessor);

        Future<?> future = executors.submit(() -> {
            for (int i = 0; i < eventNumber; i++) {
                //发布事件的标准写法——旧
                //申请当前这个事件编号；在生产者追上最小的消费者的sequence时，自旋阻塞
                long seq = ringBuffer.next();
                try {
                    double price = Math.random() * 9999;
                    //获取事件并完善事件
                    ringBuffer.get(seq).setPrice(price);
                } finally {
                    ringBuffer.publish(seq);
                }
            }
            return null;
        });

        future.get();//等待生产者结束  
        Thread.sleep(1000);
        tradeProcessor.halt();
        executors.shutdown();
    }
}  