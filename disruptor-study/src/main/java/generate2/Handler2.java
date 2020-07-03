package generate2;

import com.lmax.disruptor.EventHandler;
import generate1.Trade;
import util.PrintThread;

public class Handler2 implements EventHandler<Trade> {
	  
    @Override  
    public void onEvent(Trade event, long sequence,  boolean endOfBatch) throws Exception {  
    	System.out.println("handler2: set price "+PrintThread.threadAbout());
    	event.setPrice(17.0);
//    	Thread.sleep(1000);
    }  
      
}  