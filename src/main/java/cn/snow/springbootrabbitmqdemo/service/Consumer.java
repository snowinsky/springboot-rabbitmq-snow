package cn.snow.springbootrabbitmqdemo.service;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

@Component
public class Consumer {
    /**
     * https://www.jianshu.com/p/911d987b5f11
     *
     * 使用 @RabbitListener 注解标记方法，当监听到队列 debug 中有消息时则会进行接收并处理.
     * @RabbitListener 和 @RabbitHandler 搭配使用
     *   @RabbitListener 可以标在方法和类上面，若标在类上需配合 @RabbitHandler 注解一起使用
     *   @RabbitListener 标在类上面表示当有收到消息的时候，就交给 @RabbitHandler 的方法处理，具体使用哪个方法处理，根据 MessageConverter 转换后的参数类型
     *     例如，一个handler接收String，一个handler接收byte[] 重载处理
     *
     * @param order
     * @param headers
     * @param channel
     * @throws IOException
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
        System.out.println("---------收到消息，开始消费---------");
        System.out.println("订单ID：" + order.toString());

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
}
