package cn.snow.springbootrabbitmqdemo.service;

import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class Publisher {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void publish(LocalDateTime data){
        rabbitTemplate.convertAndSend(
                "exchangeName",
                "rout.ing.key",
                data.toString(),
                new CorrelationData());
    }

}
