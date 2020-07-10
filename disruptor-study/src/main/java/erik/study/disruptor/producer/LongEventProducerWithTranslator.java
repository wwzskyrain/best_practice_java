package erik.study.disruptor.producer;

import com.lmax.disruptor.EventTranslatorOneArg;
import com.lmax.disruptor.RingBuffer;
import erik.study.disruptor.event.LongEvent;

import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;


/**
 * @author erik.wang
 */
public class LongEventProducerWithTranslator {

    /**
     * 一个translator可以看做一个事件初始化器，publicEvent方法会调用它填充Event
     */
    private static final EventTranslatorOneArg<LongEvent, ByteBuffer> TRANSLATOR =
            (LongEvent event, long sequence, ByteBuffer buffer) -> event.setValue(buffer.getLong(0));
    private final RingBuffer<LongEvent> ringBuffer;

    public LongEventProducerWithTranslator(RingBuffer<LongEvent> ringBuffer) {
        this.ringBuffer = ringBuffer;
    }

    public void productEvent(int count)  {

        ByteBuffer byteBuffer = ByteBuffer.allocate(8);
        for (long l = 0; l < count; l++) {
            byteBuffer.putLong(0, l);
            ringBuffer.publishEvent(TRANSLATOR, byteBuffer);


            try {
                // 注释掉这一步会导致 batchSize
                TimeUnit.MILLISECONDS.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


}
