package erik.best.practice.rabbitmq;

import com.rabbitmq.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;


/**
 * @author erik.wang
 * @date 2020-04-29 21:52
 * autoAck机制：服务端把消息写到socket就会不再保存消息了，不管client是否处理成功
 */
public class TestAutomaticAckReceiver {

    private static final Logger logger = LoggerFactory.getLogger(TestAutomaticAckReceiver.class);
    public static final String EXCHANGE_NAME_POST_FIX = "automatic.ack";


    public static void main(String[] args) throws IOException {

        Channel channel = ChannelFactory.newChannelWithLocalRabbitMqServer();
        String queueName = CommonConstants.EXCHANGE_PREFIX + EXCHANGE_NAME_POST_FIX;
        String exchangeName = queueName;
        channel.queueDeclare(queueName, true, false, false, null);
        channel.queueBind(queueName, exchangeName, "_");

        //1.开启'autoAck'模式
        String consumeTag = channel.basicConsume(queueName, true, new Consumer() {

            public void handleConsumeOk(String consumerTag) {
                logger.info("ok");
            }

            public void handleCancelOk(String consumerTag) {
                logger.info("ok");
            }

            public void handleCancel(String consumerTag) throws IOException {
                logger.info("ok");
            }

            public void handleShutdownSignal(String consumerTag, ShutdownSignalException sig) {
                logger.info("ok");
            }

            public void handleRecoverOk(String consumerTag) {
                logger.info("ok");
            }

            //2.抛出RuntimeException，表示消费失败。
            //3.验证：这些消息，queue里不会保留的。
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body);
                logger.warn("fail_to_process, message={}", message);
                throw new RuntimeException("fail_to_process");
            }
        });

        try {
            TimeUnit.SECONDS.sleep(20);
            channel.basicCancel(consumeTag);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
