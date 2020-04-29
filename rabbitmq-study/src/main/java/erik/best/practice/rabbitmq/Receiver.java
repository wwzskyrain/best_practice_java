package erik.best.practice.rabbitmq;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;


/**
 * @author erik.wang
 * @date 2020-04-29 21:52
 */
public class Receiver {

    private static final Logger logger = LoggerFactory.getLogger(Receiver.class);

    private Channel channel;

    public Receiver() {

        channel = ChannelFactory.newChannelWithLocalRabbitMqServer();

    }

    public void registerMessageListener(String queueName) throws IOException {
        channel.basicConsume(queueName, true, new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {

                ObjectMapper mapper = new ObjectMapper();

                logger.info("consumerTag:{}", consumerTag);
                logger.info("envelope:{}", mapper.writeValueAsString(envelope));
                logger.info("properties:{}", mapper.writeValueAsString(properties));
                String message = new String(body);
                logger.info("message:{} \n\n\n\n", message);


            }
        });
    }

    public static void main(String[] args) throws IOException {
        Receiver receiver = new Receiver();
        receiver.registerMessageListener("best.practice.queue.erik");
    }
}
