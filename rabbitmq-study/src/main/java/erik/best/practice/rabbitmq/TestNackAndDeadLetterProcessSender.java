package erik.best.practice.rabbitmq;


import com.rabbitmq.client.BuiltinExchangeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author erik.wang
 * @date 2020-05-28 08:52
 */
public class TestNackAndDeadLetterProcessSender {

    private static final Logger logger = LoggerFactory.getLogger(TestNackAndDeadLetterProcessSender.class);

    public static void main(String[] args) {

        String exchangeName = CommonConstants.EXCHANGE_PREFIX + TestNackAndDeadLetterProcessReceiver.EXCHANGE_NAME_POST_FIX;

        Sender sender = new Sender(exchangeName, BuiltinExchangeType.FANOUT);
        for (int i = 0; i < 50; i++) {

            String message = String.format("message:%5d", i);

            sender.sendMessage("routingKey", message);

            logger.info("success_send {}", message);
        }

    }

}
