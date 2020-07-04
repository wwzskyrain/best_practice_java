package base.handler;

import com.lmax.disruptor.WorkHandler;
import event.AppendEvent;

/**
 * @author erik.wang
 * @date 2020-07-04 22:19
 */
public class AppendEventWorkHandler1 implements WorkHandler<AppendEvent> {

    private String name;

    public AppendEventWorkHandler1(String name) {
        this.name = name;
    }

    @Override
    public void onEvent(AppendEvent event) throws Exception {
        event.handleMe(name);
    }
}
