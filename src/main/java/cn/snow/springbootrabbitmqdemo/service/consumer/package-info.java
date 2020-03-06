/*@RabbitListener 有如下的属性

一个endpoint下的所有的ListenerContainer的唯一标识。没指定的话会自动生成一个。
String id() default "";

RabbitListenerContainerFactory的bean名字。
String containerFactory() default "";

queue的名字，或者是queue的bean的名字，一定得真实存在。可以写多个，用逗号分隔。
String[] queues() default {};


如果Application context下有RabbitAdmins，这个queue会被声明在broker中，使用默认的exchange，routing key就是queue的名字。
Queue[] queuesToDeclare() default {};


true=只有一个consumer在这个container里
boolean exclusive() default false;


表示优先级，数越大优先级越高，正数负数都行
String priority() default "";


RabbitAdmin的bean的名字。如果设定listener是auto-delete，这个必须设置
String admin() default "";

这是个例子，就是把各种绑定信息汇总在这里。而且可以写多个。
        bindings = @QueueBinding(
        value = @Queue(value = "queueName", durable = "true"),//指定了queue的名字
        exchange = @Exchange(name = "exchangeName", durable = "true", type = "topic"), //指定exchange的一些参数
        key = "order.*") //binding key
QueueBinding[] bindings() default {};


这个listener container会被加到一个bean里bean名字就是这个
String group() default "";

true=直接扔exception，false=包装的异常
String returnExceptions() default "";


RabbitListenerErrorHandler类型的bean的名字
String errorHandler() default "";



SimpleMessage--> n：表示并发consumer个数。m-n：最大最小值
DirectMessage--> n: 表示一个queue下的consumer数
String concurrency() default "";


Set to true or false, to override the default setting in the container factory.
String autoStartup() default "";

* Set the task executor bean name to use for this listener's container; overrides
* any executor set on the container factory.
* @return the executor bean name.

String executor() default "";

* Override the container factory
* {@link org.springframework.amqp.core.AcknowledgeMode} property. Must be one of the
* valid enumerations. If a SpEL expression is provided, it must evaluate to a
* {@link String} or {@link org.springframework.amqp.core.AcknowledgeMode}.
* @return the acknowledgement mode.
String ackMode() default "";

* The bean name of a
* {@link org.springframework.amqp.rabbit.listener.adapter.ReplyPostProcessor} to post
* process a response before it is sent.
* @return the bean name.
String replyPostProcessor() default "";*/
package cn.snow.springbootrabbitmqdemo.service.consumer;