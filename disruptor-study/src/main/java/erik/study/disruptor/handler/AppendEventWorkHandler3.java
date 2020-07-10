package erik.study.disruptor.handler;

import com.lmax.disruptor.WorkHandler;
import erik.study.disruptor.event.AppendEvent;

/**
 * @author erik.wang
 * @date 2020-07-04 22:19
 */
public class AppendEventWorkHandler3 implements WorkHandler<AppendEvent> {

    private String name;

    public AppendEventWorkHandler3(String name) {
        this.name = name;
    }

    @Override
    public void onEvent(AppendEvent event) throws Exception {
        event.handleMe(name);
        System.out.println(event);
    }
}
