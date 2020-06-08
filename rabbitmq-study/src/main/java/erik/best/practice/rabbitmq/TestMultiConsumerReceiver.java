package erik.best.practice.rabbitmq;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.Envelope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author erik.wang
 * @date 2020-05-28 13:01
 * 验证多消费者：
 * case1.一个channel注册两个consumer
 * 1.   两个consumer的话，会轮训push消息
 * <p>
 * case2.两个channel分别注册一个consumer
 * 1.   两个channel也会轮训push消息
 * <p>
 * case3. case1 + case2
 * 1.  也会轮训
 * <p>
 * 其他的经验
 * 1.   Consumer接口中的好几个回调方法，通过看注释，和打日志，就能明白他们的声明周期了。
 * 2.   consumer的处理函数，是在一个线程池中执行的；这个线程池目前不是'开发者'配置的。
 * 3.   qos除了prefetch之外还有一个prefetchSize(单位是字节)和global标志——true表示该channel的所有consumer
 * 4.   qos表示滑动窗口的大小(单位是消息个数，而不是字节)
 */
public class TestMultiConsumerReceiver {

    private static final Logger logger = LoggerFactory.getLogger(TestMultiConsumerReceiver.class);

    public static final String EXCHANGE_NAME_POST_FIX = "multi.consumer";


    public static void main(String[] args) {

        final Channel channel1 = ChannelFactory.newChannelWithLocalRabbitMqServer();
        try {
            channel1.basicQos(2);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String queueName = CommonConstants.EXCHANGE_PREFIX + EXCHANGE_NAME_POST_FIX;
        String exchangeName = queueName;

        try {
            channel1.queueDeclare(queueName, true, false, false, null);
            channel1.queueBind(queueName, exchangeName, "");
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            channel1.basicConsume(queueName, new ConsumerAdapter() {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    waite(1);
                    logger.info("consumerTag={} message={}", consumerTag, new String(body));
                    channel1.basicAck(envelope.getDeliveryTag(), false);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            channel1.basicQos(1);
            channel1.basicConsume(queueName, new ConsumerAdapter() {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    waite(1);
                    logger.info("consumerTag={} message={}", consumerTag, new String(body));
                    channel1.basicAck(envelope.getDeliveryTag(), false);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }


        final Channel channel = ChannelFactory.newChannelWithLocalRabbitMqServer();
        try {
            channel.basicQos(1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            channel.basicConsume(queueName, new ConsumerAdapter() {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    waite(1);
                    logger.info("consumerTag={} message={}", consumerTag, new String(body));
                    channel.basicAck(envelope.getDeliveryTag(), false);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void waite(int seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
