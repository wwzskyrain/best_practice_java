package generate2;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.WorkHandler;
import generate1.Trade;
import util.PrintThread;

public class Handler1 implements EventHandler<Trade>, WorkHandler<Trade> {

    @Override
    public void onEvent(Trade event, long sequence, boolean endOfBatch) throws Exception {
        this.onEvent(event);
    }

    @Override
    public void onEvent(Trade event) throws Exception {

        System.out.println("handler1: set name, " + PrintThread.threadAbout());
        event.setName("h1");
        Thread.sleep(1000);
    }
}  