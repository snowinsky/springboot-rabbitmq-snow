package com.snow.mustsendmustconsume.rabbitmq;

import com.snow.mustsendmustconsume.config.RabbitMqConfig;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

public class Producer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendIntoQueue() {
        String msgId = generateUuID();
        CorrelationData correlationData = new CorrelationData(msgId);
        String exchangeName = RabbitMqConfig.MAIL_EXCHANGE_NAME;
        String routingKey = RabbitMqConfig.MAIL_ROUTING_KEY_NAME;
        String jsonMessage = "{}";
        rabbitTemplate.convertAndSend(
                exchangeName,
                routingKey,
                jsonMessage,
                correlationData
        );
    }

    private String generateUuID() {
        return null;
    }
}
