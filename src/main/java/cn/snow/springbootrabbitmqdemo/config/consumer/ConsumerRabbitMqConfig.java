package cn.snow.springbootrabbitmqdemo.config.consumer;

import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.api.RabbitListenerErrorHandler;
import org.springframework.amqp.rabbit.support.ListenerExecutionFailedException;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ErrorHandler;

@Configuration
public class ConsumerRabbitMqConfig {

    @Bean
    public ConnectionFactory connectionFactory() {
        final String host = "";
        final int port = 5734;
        final String username = "";
        final String password = "";
        final String virtualHost = "vhost";
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(host, port);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        connectionFactory.setVirtualHost(virtualHost);
        return connectionFactory;
    }

    @Bean(name = "consumerListenerContainerFactory")
    public RabbitListenerContainerFactory<SimpleMessageListenerContainer> listenerFactory() {
        SimpleRabbitListenerContainerFactory listenerFactory = new SimpleRabbitListenerContainerFactory();
        //允许同时消费数量为5
        listenerFactory.setConcurrentConsumers(5);
        //允许同时最大消费数量为10
        listenerFactory.setMaxConcurrentConsumers(10);
        //预获取的消息数量
        listenerFactory.setPrefetchCount(20);
        //设置接收消息的超时时间10秒
        listenerFactory.setReceiveTimeout(10000L);
        //这里必须设置和生产者相同的MessageConverter，这样接收的消息被反序列化为对象
        listenerFactory.setMessageConverter(new Jackson2JsonMessageConverter());
        //设置手动提交，这样设定就不会消息一旦被拿到消费者端就从MQ中删除了，可以避免消息丢失
        listenerFactory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        //设置监听的是哪个connection的，当然connection是用factory来池化
        listenerFactory.setConnectionFactory(connectionFactory());
        return listenerFactory;
    }

}
