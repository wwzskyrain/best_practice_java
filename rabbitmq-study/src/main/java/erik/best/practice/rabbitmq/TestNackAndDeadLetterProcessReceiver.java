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
        channel.queueBind(deadLetterExName, deadLetterQueueName, "any_routing_key");

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
