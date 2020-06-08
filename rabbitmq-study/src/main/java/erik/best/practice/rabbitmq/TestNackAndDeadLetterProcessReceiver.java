package erik.best.practice.rabbitmq;


import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Envelope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author erik.wang
 * @date 2020-05-28 14:34
 * 验证：
 * 1.   给队列声明死信exchange以及死信队列
 * 2.   触发死信处理的三种情况
 *      1.  被应用系统用拒绝命令(basic.reject或者basic.nack)处理的消息，并且'requeue'标志是false，就会触发死信机制
 *      2.  一个在队列中呆了ttl时间之后的消息
 *      3.  当队列满了之后，溢出的消息
 *      4.  这里只验证了第一中情况。
 *
 */
public class TestNackAndDeadLetterProcessReceiver {

    private static final Logger logger = LoggerFactory.getLogger(TestNackAndDeadLetterProcessReceiver.class);

    public static final String EXCHANGE_NAME_POST_FIX = "nack.not.requeue.dead.letter";

    public static void main(String[] args) throws IOException {

        final Channel channel = ChannelFactory.newChannelWithLocalRabbitMqServer();
        String queueName = CommonConstants.EXCHANGE_PREFIX + EXCHANGE_NAME_POST_FIX;

        String deadLetterExName = queueName + ".dl.exchange";
        channel.exchangeDeclare(deadLetterExName, BuiltinExchangeType.FANOUT);

        String deadLetterQueueName = queueName + ".dl.queue";
        channel.queueDeclare(deadLetterQueueName, true, false, false, null);
        channel.queueBind(deadLetterQueueName, deadLetterExName, "any_routing_key");

        Map<String, Object> queueArgs = new HashMap<String, Object>();
        queueArgs.put("x-dead-letter-exchange", deadLetterExName);
        channel.queueDeclare(queueName, true, false, false, queueArgs);
        channel.queueBind(queueName, queueName, "any_routing_key");

        channel.basicConsume(queueName, new ConsumerAdapter() {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {

                String message = new String(body);
                if (message.endsWith("4")) {
                    channel.basicNack(envelope.getDeliveryTag(), false, false);
                    logger.info("nack_message={}", message);
                } else {
                    channel.basicAck(envelope.getDeliveryTag(), false);
                    logger.info("ack_message={}", message);
                }
            }
        });
    }

}
