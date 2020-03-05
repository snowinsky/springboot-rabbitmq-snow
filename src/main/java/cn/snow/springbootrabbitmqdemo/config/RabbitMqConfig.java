package cn.snow.springbootrabbitmqdemo.config;

import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory rabbitConnectionFactory, RabbitConfirmCallBack confirmCallBack) {
        RabbitTemplate rt = new RabbitTemplate(rabbitConnectionFactory);
        rt.setConfirmCallback(confirmCallBack);
        return rt;
    }

    @Bean
    public ConnectionFactory rabbitConnectionFactory() {
        CachingConnectionFactory factory = new CachingConnectionFactory();
        factory.setAddresses("");
        factory.setUsername("");
        factory.setVirtualHost("");
        factory.setPassword("");
        factory.setPublisherConfirmType(CachingConnectionFactory.ConfirmType.CORRELATED);
        return factory;
    }
}
