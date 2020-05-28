package erik.best.practice.rabbitmq.confrim.send;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmCallback;
import com.rabbitmq.client.ConfirmListener;
import erik.best.practice.rabbitmq.ChannelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author erik.wang
 * @date 2020-05-06 10:08
 * rabbitMq的发送确认
 */
public class SendWithConfrim {

    private static final Logger logger = LoggerFactory.getLogger(SendWithConfrim.class);

    private Channel channel;
    private String exchangeName;

    public SendWithConfrim(Channel channel, String exchangeName) {
        this.channel = ChannelFactory.newChannelWithLocalRabbitMqServer();
        this.exchangeName = exchangeName;
    }

    public void sendWithConfirm() {
        try {
            channel.basicPublish(exchangeName, "routing-key", null, null);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //        channel.basicPublish(exchangeName, "routing-key", false, null, null);
//        channel.basicPublish(exchangeName, "routing-key", false, false, null, null);

        channel.addConfirmListener(new ConfirmListener() {
            public void handleAck(long deliveryTag, boolean multiple) {
                logger.info("deliveryTag:{}, multiple:{}", deliveryTag, multiple);
            }

            public void handleNack(long deliveryTag, boolean multiple) {
                logger.info("deliveryTag:{}, multiple:{}", deliveryTag, multiple);
            }
        });
    }


}
