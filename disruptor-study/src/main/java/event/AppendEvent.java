package event;

import com.lmax.disruptor.EventHandler;


/**
 * @author erik.wang
 * @date 2020-07-04 10:54
 */
public class AppendEvent {

    private String track = AppendEvent.class.getSimpleName();

    /**
     * 不加synchronize关键字的话，赋值track会被覆盖
     *
     * @param c
     */
    public synchronized void handleMe(Class<?> c) {
        track = String.format("%s->[%s]", track, c.getSimpleName().substring(11));
    }

    /**
     * 不加synchronize关键字的话，赋值track会被覆盖
     */
    public synchronized void handleMe(String handlerName) {
        track = String.format("%s->[%s]", track, handlerName);
    }


    @Override
    public String toString() {
        return "AppendEvent{" +
                "track='" + track + '\'' +
                '}';
    }

}
