package erik.best.practice.rabbitmq;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.ShutdownSignalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author erik.wang
 * @date 2020-05-28 13:10
 */
public class ConsumerAdapter implements Consumer {

    private static final Logger logger = LoggerFactory.getLogger(ConsumerAdapter.class);

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

    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {

    }
}
