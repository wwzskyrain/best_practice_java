package generate2;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.WorkHandler;
import generate1.Trade;
import util.PrintThread;

import java.util.concurrent.TimeUnit;

public class Handler4 implements EventHandler<Trade>, WorkHandler<Trade> {

    @Override
    public void onEvent(Trade event, long sequence, boolean endOfBatch) throws Exception {
        this.onEvent(event);
    }

    @Override
    public void onEvent(Trade event) throws Exception {
        System.out.println("handler4: get name : " + event.getName() + PrintThread.threadAbout());
        event.setName(event.getName() + "h4");
        TimeUnit.SECONDS.sleep(1);
    }
}  