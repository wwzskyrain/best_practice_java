package event;

import com.lmax.disruptor.EventFactory;
import event.LongEvent;

/**
 * @author
 */

public class LongEventFactory implements EventFactory {

    @Override
    public Object newInstance() {
        return new LongEvent();
    }
} 