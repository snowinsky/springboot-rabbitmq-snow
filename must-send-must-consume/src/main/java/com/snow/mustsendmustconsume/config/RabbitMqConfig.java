package com.snow.mustsendmustconsume.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class RabbitMqConfig {

    @Autowired
    private CachingConnectionFactory connectionFactory;

    @Bean
    public RabbitTemplate rabbitTemplate() {
        //设置RabbitMQ的连接池
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        //设置RabbtiMQ的消息的转换方式，这里是使用Jackson实现Json字符串与对象之间的转换
        rabbitTemplate.setMessageConverter(converter());

        // 消息是否成功从生产者发送到Exchange
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                log.info("消息成功发送到Exchange");
                String msgId = correlationData.getId();
                log.info("消息{}已成功发送到Exchange，消息状态修改为‘生产者发动消息到Exchange成功’ ", msgId);
                //TODO 更新数据库中该消息的状态为“生产者发送消息到Exchange成功”
            } else {
                log.info("消息发送到Exchange失败, {}, cause: {}", correlationData, cause);
                //TODO  更新数据库中该消息的状态为“生产者发送消息到Exchange失败”
            }
        });

        // 触发setReturnCallback回调必须设置mandatory=true, 否则Exchange没有找到Queue就会丢弃掉消息, 而不会触发回调
        rabbitTemplate.setMandatory(true);
        // 消息是否从Exchange路由到Queue, 注意: 这是一个失败回调, 只有消息从Exchange路由到Queue失败才会回调这个方法
        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
            log.info("消息从Exchange路由到Queue失败: exchange: {}, route: {}, replyCode: {}, replyText: {}, message: {}", exchange, routingKey, replyCode, replyText, message);
            //TODO 更新数据库中消息的状态为“Exchange发送到Queue失败”
        });

        return rabbitTemplate;
    }

    @Bean
    public Jackson2JsonMessageConverter converter() {
        return new Jackson2JsonMessageConverter();
    }

    public static final String LOGIN_LOG_QUEUE_NAME = "login.log.queue";
    public static final String LOGIN_LOG_EXCHANGE_NAME = "login.log.exchange";
    public static final String LOGIN_LOG_ROUTING_KEY_NAME = "login.log.routing.key";

    @Bean
    public Queue durableQueue() {
        boolean isQueueDurable = true;
        return new Queue(LOGIN_LOG_QUEUE_NAME, isQueueDurable);
    }

    @Bean
    public DirectExchange durableExchange() {
        boolean isExchangeDurable = true;
        boolean isExchangeAutoDelete = false;
        return new DirectExchange(LOGIN_LOG_EXCHANGE_NAME, isExchangeDurable, isExchangeAutoDelete);
    }

    @Bean
    public Binding durableExchangeBindingDurableQueue() {
        return BindingBuilder.bind(durableQueue()).to(durableExchange()).with(LOGIN_LOG_ROUTING_KEY_NAME);
    }

    // 发送邮件
    public static final String MAIL_QUEUE_NAME = "mail.queue";
    public static final String MAIL_EXCHANGE_NAME = "mail.exchange";
    public static final String MAIL_ROUTING_KEY_NAME = "mail.routing.key";

    @Bean
    public Queue mailQueue() {
        return new Queue(MAIL_QUEUE_NAME, true);
    }

    @Bean
    public DirectExchange mailExchange() {
        return new DirectExchange(MAIL_EXCHANGE_NAME, true, false);
    }

    @Bean
    public Binding mailBinding() {
        return BindingBuilder.bind(mailQueue()).to(mailExchange()).with(MAIL_ROUTING_KEY_NAME);
    }

}
