package erik.best.practice.rabbitmq;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;


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
    private BuiltinExchangeType exchangeType;


    public Sender(String exchangeName, BuiltinExchangeType exchangeType) {
        this.channel = ChannelFactory.newChannelWithLocalRabbitMqServer();
        this.exchangeName = exchangeName;
        this.exchangeType = exchangeType;
        try {
            channel.exchangeDeclare(exchangeName, exchangeType, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String routingKey, String message) {

        try {
            channel.basicPublish(exchangeName, routingKey, null, message.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException, TimeoutException {
        Sender sender = new Sender("best.practice.exchange.first", BuiltinExchangeType.FANOUT);
        for (int i = 0; i < 10; i++) {
            String message = String.format("message.%d", i);
            sender.sendMessage("erik", message);
            logger.info("success to send message :{}", message);
        }
//      运行完之后，程序不会退出的，为什么？
    }


}
