package cn.snow.springbootrabbitmqdemo.service.publisher;

import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class Publisher {

    @Autowired
    @Qualifier(value = "publisherRabbitMq")
    private RabbitTemplate rabbitTemplate;

    public void publish(LocalDateTime data){
        rabbitTemplate.convertAndSend(
                //必须和定义的设置的生产者MQ的一样
                "publisherRabbitMqExchange",
                //必须和定义的设置的生产者MQ的相一致
                "publisher.rabbitmq.exchange.test.001",
                data.toString(),
                new CorrelationData());
    }

}
