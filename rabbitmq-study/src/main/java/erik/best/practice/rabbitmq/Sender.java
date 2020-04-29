package erik.best.practice.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author erik.wang
 * @date 2020-04-29 21:03
 */
public class Sender {

    private static final Logger logger = LoggerFactory.getLogger(Sender.class);

    private Channel channel;
    private String exchangeName;

    public Sender(String exchangeName) {
        this.channel = ChannelFactory.newChannelWithLocalRabbitMqServer();
        this.exchangeName = exchangeName;

    }

    public void sendMessage(String routingKey, String message) throws IOException {
        channel.basicPublish(exchangeName, routingKey, null, message.getBytes());
    }

    public static void main(String[] args) throws IOException, TimeoutException {
        Sender sender = new Sender("best.practice.exchange.first");
        for (int i = 0; i < 10; i++) {
            String message = String.format("message.%d", i);
            sender.sendMessage("erik", message);
            logger.info("success to send message :{}", message);
        }
//      运行完之后，程序不会退出的，为什么？
    }


}
