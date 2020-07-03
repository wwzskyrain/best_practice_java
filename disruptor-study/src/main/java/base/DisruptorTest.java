package base;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.EventTranslator;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import util.PrintThread;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author
 */
public class DisruptorTest {
    public static void main(String[] args) {
        int bufferSize = 1024;
        Disruptor<Event> disruptor = new Disruptor<>(
                Event::new, bufferSize, Executors.defaultThreadFactory()
        );

        // 1
        disruptor
                .handleEventsWith(new Consumer("A"), new Consumer("B"))
                .then(new Consumer("C"));

        disruptor.start();
        produceSomeEvents(disruptor);
        disruptor.shutdown();
    }

    private static void produceSomeEvents(Disruptor<Event> disruptor) {
        // 2
        RingBuffer<Event> ringBuffer = disruptor.getRingBuffer();
        Producer producer = new Producer();
        for (int i = 0; i < 10; i++) {
            ringBuffer.publishEvent(producer);
            System.out.println("produce:" + i + PrintThread.threadAbout());
        }

        for (int i = 10; i < 20; i++) {
            ringBuffer.publishEvent(producer);
            System.out.println("produce:" + i + PrintThread.threadAbout());
        }
    }

    public static class Event {
        private int value;
    }

    public static class Producer implements EventTranslator<Event> {
        private int i = 0;

        @Override
        public void translateTo(Event event, long sequence) {
            event.value = i++;
        }
    }

    public static class Consumer implements EventHandler<Event> {
        private final String name;

        public Consumer(String name) {
            this.name = name;
        }

        @Override
        public void onEvent(Event event, long sequence, boolean endOfBatch) throws Exception {
            System.out.println(name + ": " + event.value);
            TimeUnit.SECONDS.sleep(1);
        }
    }
}