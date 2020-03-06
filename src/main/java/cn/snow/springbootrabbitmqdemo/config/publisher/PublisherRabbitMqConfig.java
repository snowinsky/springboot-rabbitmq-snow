package cn.snow.springbootrabbitmqdemo.config.publisher;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PublisherRabbitMqConfig {

    @Bean(name = "publisherRabbitMq")
    public RabbitTemplate rabbitTemplate(ConnectionFactory rabbitConnectionFactory, PublisherRabbitConfirmCallBack confirmCallBack) {
        RabbitTemplate rt = new RabbitTemplate(rabbitConnectionFactory);
        //ConnectionFactory如果设置setPublisherConfirmType并不是none，则需要设置ConfirmCallback
        rt.setConfirmCallback(confirmCallBack);
        //ConnectionFactory如果设置setPublisherReturns是true，则需要设置ConfirmCallback
        rt.setReturnCallback(new RabbitTemplate.ReturnCallback() {
            @Override
            public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
                //TODO
            }
        });
        //默认是使用jdk自带的序列化工具，性能较差。如果是json格式的，可以使用下面的方式。
        rt.setMessageConverter(new Jackson2JsonMessageConverter());
        //绑定exchange
        rt.setExchange("publisherRabbitMqExchange");
        rt.setRoutingKey("publisher.rabbitmq.exchange.test.001");
        rt.setDefaultReceiveQueue("queueName001");
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
        factory.setPublisherReturns(true);
        return factory;
    }
}
