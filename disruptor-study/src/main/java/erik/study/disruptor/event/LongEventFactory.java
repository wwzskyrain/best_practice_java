package erik.study.disruptor.event;

import com.lmax.disruptor.EventFactory;

/**
 * @author
 */

public class LongEventFactory implements EventFactory {

    @Override
    public Object newInstance() {
        return new LongEvent();
    }
} 