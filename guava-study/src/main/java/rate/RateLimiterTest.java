package rate;

import com.google.common.util.concurrent.RateLimiter;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

/**
 * @author erik.wang
 * @date 2020-06-11 08:33
 */
public class RateLimiterTest {

    private static final Logger logger = LoggerFactory.getLogger(RateLimiterTest.class);

    @Test
    public void test_smooth_bursty() {
        RateLimiter rateLimiter = RateLimiter.create(5);
        while (true) {
//            .acquire() 是阻塞式的
            logger.info("get token: {}s", rateLimiter.acquire());
        }
    }


    /**
     * 每1/permitsPerSecond 的时间是一个发放token的点，姑且称作'token点'。
     * acquire(n)是赊账式的，但是只能连续赊账一次，即不能在赊账的时候继续赊账，必须等账款还清才能继续赊账。
     * 赊的账，只能用时间来偿还。
     * 比如，在一个token点，如果用户没有赊账了，或者账款在这个token点还清了(时间到了)；
     * 用户就可以赊一个任意正金额的账；即当acquire(n)中的n很大时，也会立即返回；
     * 要等待的是谁呢，是下一个来赊账的acquire(1)，这是n=1，即使赊一笔小账，也要等，等上一笔账款还清。
     * <p>
     * <p>
     * 哦，原来赊账式，也可以理解为'令牌桶'式。不，不能用令牌桶模型来解释。
     * 因为在p4处，是不需要等待2秒的，而是p5要等2秒，即先消费后还款。
     * 而不是先有了足够的令牌，再来消费这令牌。
     * <p>
     * 但是这种大额赊账，不是限流器正常的工作场景；
     * <p>
     * 会累积一秒的量
     */
    @Test
    public void test_request_wth_five() {

        logger.info("start");
        RateLimiter r = RateLimiter.create(5);
        sleep(5);
        getToken(1, r); //p1
        getToken(4, r); //p2
        getToken(1, r); //p3
        getToken(10, r); //p4
        getToken(1, r); //p5

    }

    /**
     * 能储备吗，能的，但是最多只能储备一个单位时间。每一个token点都可能开始储备。
     */
    @Test
    public void test_request_wth_reserve() {

        logger.info("start");
        RateLimiter r = RateLimiter.create(5);
//        sleep(1);
        for (int i = 0; i < 20; i++) {
            if (i == 8) {
                //i=8时，储备一秒
                sleep(1);
            }
            getTokenWithNo(i, 1, r);
        }
    }


    private void sleep(Integer seconds) {
        logger.info("sleep {} seconds !", seconds);
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void getToken(int token, RateLimiter r) {

        long now = System.currentTimeMillis();
        logger.info("get {} token", token);
        double acquire = r.acquire(token);
        logger.info("get {} token, take {}s, acquire {}s", token, (System.currentTimeMillis() - now) / 1000.0, acquire);
    }

    private void getTokenWithNo(int no, int token, RateLimiter r) {

        long now = System.currentTimeMillis();
        logger.info("no.{} get {} token", no, token);
        logger.info("no.{} get {} token, take {}s, acquire {}s", no, token, (System.currentTimeMillis() - now) / 1000.0, r.acquire(token));
    }

    static class RequestToken extends Thread {

        private RateLimiter r;
        private int tokenCount;

        public RequestToken(RateLimiter r, int tokenCount) {
            this.r = r;
            this.tokenCount = tokenCount;
        }

        @Override
        public void run() {
            getToken(tokenCount, r);
        }
    }

    /**
     * 测试再多线程下的赊账模型：前面线程舍得账，需要后面的请求者来等待。
     */
    @Test
    public void test_accumulate_wait_time() {

        RateLimiter r = RateLimiter.create(5);

        Executor executor = new ThreadPoolExecutor(10, 10, 100, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(20));
        for (int i = 0; i < 5; i++) {
            executor.execute(new RequestToken(r, 4));
        }

        try {
            TimeUnit.SECONDS.sleep(20);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        logger.info("over");
    }


}
