package event;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author erik.wang
 * @date 2020-07-04 22:03
 */
public class NumberEvent {

    private static final AtomicInteger generator = new AtomicInteger(0);

    private int id;

    public void setId() {
        this.id = generator.incrementAndGet();
    }

    @Override
    public String toString() {
        return "NumberEvent{" +
                "id=" + id +
                '}';
    }
}
