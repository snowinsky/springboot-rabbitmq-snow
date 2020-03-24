package com.snow.mustsendmustconsume.rabbitmq;

import com.rabbitmq.client.Channel;
import com.snow.mustsendmustconsume.config.RabbitMqConfig;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

import java.io.IOException;

public class Consumer {

    @RabbitListener(queues = {RabbitMqConfig.MAIL_QUEUE_NAME})
    public void consume(Message msg, Channel channel) throws IOException {
        byte[] bytes = msg.getBody();
        String msgId = extractorMsgId(bytes);

        String msgStatus = getMsgStatus(msgId);
        if(msgStatus.equals("没找到该消息的信息") || msgStatus.equals("该消息已成功消费")){
            return;
        }

        MessageProperties msgProperties = msg.getMessageProperties();
        long tag= msgProperties.getDeliveryTag();
        
        boolean isConsumeSuccess = consume(bytes);
        if(isConsumeSuccess){
            //TODO 更新该消息的数据库状态为“已成功消费”
            boolean isAckTheMsgLessThanTag = false;
            channel.basicAck(tag, isAckTheMsgLessThanTag);
        }else{
            //TODO 更新该消息的数据库状态为“消费失败”
            boolean isAckTheMsgLessThanTag = false;
            boolean isReturnBackToQueue = true;
            channel.basicNack(tag, isAckTheMsgLessThanTag, isReturnBackToQueue);
        }
    }

    private boolean consume(byte[] bytes) {
        return false;
    }

    private String getMsgStatus(String msgId) {
        return null;
    }

    private String extractorMsgId(byte[] bytes) {
        return null;
    }
}
