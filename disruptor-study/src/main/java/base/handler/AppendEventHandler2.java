package base.handler;

import com.lmax.disruptor.EventHandler;
import event.AppendEvent;

/**
 * @author erik.wang
 * @date 2020-07-04 11:26
 */
public class AppendEventHandler2 implements EventHandler<AppendEvent> {

    @Override
    public void onEvent(AppendEvent event, long sequence, boolean endOfBatch) throws Exception {
        event.handleMe(AppendEventHandler2.class);
    }
}
