package cn.snow.springbootrabbitmqdemo.service.consumer;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class Consumer {
    /**
     * https://www.jianshu.com/p/911d987b5f11
     * <p>
     * 使用 @RabbitListener 注解标记方法，当监听到队列 debug 中有消息时则会进行接收并处理.
     *
     * @param order
     * @param headers
     * @param channel
     * @throws IOException
     * @RabbitListener 和 @RabbitHandler 搭配使用
     * @RabbitListener 可以标在方法和类上面，若标在类上需配合 @RabbitHandler 注解一起使用
     * @RabbitListener 标在类上面表示当有收到消息的时候，就交给 @RabbitHandler 的方法处理，具体使用哪个方法处理，根据 MessageConverter 转换后的参数类型
     * 例如，一个handler接收String，一个handler接收byte[] 重载处理
     */
    @RabbitListener(  //该注解标识该方法是用来消费消息的方法
            bindings = @QueueBinding(
                    value = @Queue(value = "queueName", durable = "true"),//指定了queue的名字
                    exchange = @Exchange(name = "exchangeName", durable = "true", type = "topic"), //指定exchange的一些参数
                    key = "order.*") //binding key
    )
    @RabbitHandler//如果有消息过来，在消费的时候调用这个方法
    public void onOrderMessage(@Payload LocalDateTime order, @Headers Map<String, Object> headers, Channel channel) throws IOException {
        //消费者操作
        log.info("---------收到消息，开始消费---------");
        log.info("订单ID：" + order.toString());

        /**
         * Delivery Tag 用来标识信道中投递的消息。RabbitMQ 推送消息给 Consumer 时，会附带一个 Delivery Tag，
         * 以便 Consumer 可以在消息确认时告诉 RabbitMQ 到底是哪条消息被确认了。
         * RabbitMQ 保证在每个信道中，每条消息的 Delivery Tag 从 1 开始递增。
         */
        Long deliveryTag = (Long) headers.get(AmqpHeaders.DELIVERY_TAG);

        /**
         *  multiple 取值为 false 时，表示通知 RabbitMQ 当前消息被确认
         *  如果为 true，则额外将比第一个参数指定的 delivery tag 小的消息一并确认
         */
        boolean multiple = false;

        //ACK,确认一条消息已经被消费
        channel.basicAck(deliveryTag, multiple);
    }

    @RabbitListener(containerFactory = "consumerListenerContainerFactory", errorHandler = "RabbitConsumerListenerErrorHandler", queues = "TestDirectQueue")
    @RabbitHandler // 此注解加上之后可以接受对象型消息
    public void onDirectQueueMessage(@Payload List<String[]> orders, Channel channel, @Headers Map<String, Object> heads)
            throws Exception {

        log.info(orders.toString());
        // 此处也可以带messagid等信息作为唯一标识来确保消息的幂等操作
        /**
         * 做幂等操作可用redis或者DB来存储唯一标识符，每次消费前
         * 先查询是否消费了，如果没有消费就在消费逻辑。
         */
        log.info("消息唯一标识符:" + heads.get("number"));

        long deliveryTag = (Long) heads.get(AmqpHeaders.DELIVERY_TAG);
        log.info("The deliveryTag is {}", deliveryTag);
        // 告诉服务器，已经消费成功,
        channel.basicAck(deliveryTag, false);
    }

    @RabbitListener(containerFactory = "consumerListenerContainerFactory", errorHandler = "RabbitConsumerListenerErrorHandler", queues = "dead_queue")
    @RabbitHandler
    public void onDeadMessage2(Message message, Channel channel, @Headers Map<String, Object> heads) throws Exception {

        log.info("死信队列message" + new String(message.getBody()));
        long deTag = (Long) heads.get(AmqpHeaders.DELIVERY_TAG);

        // 正常业务逻辑，当出现异常的时候，走死信队列重新消费。

        log.info("The deliveryTag is {}", deTag);
        // 告诉服务器，已经消费成功,
        channel.basicAck(deTag, false);
    }

    @RabbitListener(containerFactory = "consumerListenerContainerFactory", errorHandler = "RabbitConsumerListenerErrorHandler", queues = "test_queue_1")
    @RabbitHandler
    public void delayMessage(Message message, Channel channel, @Headers Map<String, Object> heads) throws Exception {

        log.info("分发队列message" + new String(message.getBody()));
        long deTag = (Long) heads.get(AmqpHeaders.DELIVERY_TAG);

        // 正常业务逻辑，当出现异常的时候，走死信队列重新消费。

        log.info("The deliveryTag is {}", deTag);
        // 告诉服务器，已经消费成功,
        channel.basicAck(deTag, false);
    }

    @RabbitListener(containerFactory = "consumerListenerContainerFactory", errorHandler = "RabbitConsumerListenerErrorHandler", queues = "TestFanoutQueue")
    @RabbitHandler
    public void faountMessage(Message message, Channel channel, @Headers Map<String, Object> heads) throws Exception {

        log.info("延时队列message" + new String(message.getBody()));
        long deTag = (Long) heads.get(AmqpHeaders.DELIVERY_TAG);

        // 正常业务逻辑，当出现异常的时候，走死信队列重新消费。

        log.info("The deliveryTag is {}", deTag);
        // 告诉服务器，已经消费成功,
        channel.basicAck(deTag, false);
    }

    @RabbitListener(containerFactory = "consumerListenerContainerFactory", errorHandler = "RabbitConsumerListenerErrorHandler", queues = "TestFanoutQueue2")
    @RabbitHandler
    public void faount2Message(Message message, Channel channel, @Headers Map<String, Object> heads) throws Exception {

        log.info("分发队列message" + new String(message.getBody()));
        long deTag = (Long) heads.get(AmqpHeaders.DELIVERY_TAG);

        // 正常业务逻辑，当出现异常的时候，走死信队列重新消费。

        log.info("The deliveryTag is {}", deTag);
        // 告诉服务器，已经消费成功,
        channel.basicAck(deTag, false);
        /**
         * 其实此处可以使用basicNack，那么就不需要在添加消息的过期时间了。
         * 下面的代码可以实现，当业务逻辑出现异常的时候走死信交换机，重新消费。
         * 或者第三个参数设置为true则会重新消费(要注意幂等性，如果只有一个消费者那么
         * 说明消费者代码逻辑有误，重新消费无多大意义)
         */
    }
}
