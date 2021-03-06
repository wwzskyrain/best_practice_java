package erik.study.disruptor.handler;

import com.lmax.disruptor.EventHandler;
import erik.study.disruptor.event.AppendEvent;

/**
 * @author erik.wang
 * @date 2020-07-04 11:26
 */
public class AppendEventHandler3 implements EventHandler<AppendEvent> {

    @Override
    public void onEvent(AppendEvent event, long sequence, boolean endOfBatch) throws Exception {
        event.handleMe(AppendEventHandler3.class);
        System.out.println(event);
    }
}
