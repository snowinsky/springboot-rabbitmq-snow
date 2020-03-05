package cn.snow.springbootrabbitmqdemo.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.util.concurrent.SettableListenableFuture;

@Component
@Slf4j
public class RabbitConfirmCallBack implements RabbitTemplate.ConfirmCallback {
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        SettableListenableFuture<CorrelationData.Confirm> confirmFuture = correlationData.getFuture();
        confirmFuture.addCallback(new ListenableFutureCallback() {

            @Override
            public void onSuccess(Object o) {
                log.info("confirm future complete successfully.");
            }

            @Override
            public void onFailure(Throwable throwable) {
                log.error("confirm future complete fail.");
            }
        });

        String id = correlationData.getId();
        log.info(id);

        Message msg = correlationData.getReturnedMessage();
        byte[] bodyContent = msg.getBody();
        MessageProperties msgProp = msg.getMessageProperties();
        log.info(msgProp.getConsumerTag());

        log.info("The acknowledage flag is {}", ack);

        log.info("The reason of the nonacknowledge is {}", cause);


    }
}
