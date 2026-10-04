package cl.duoc.pedidos360.notifications_service.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String ORDERS_TOPIC_EXCHANGE = "orders.topic";
    public static final String ORDER_WILDCARD_ROUTING_KEY = "order.#";
    public static final String NOTIF_QUEUE = "notif-queue";
    public static final String NOTIF_QUEUE_DLQ = "notif-queue.dlq";
    public static final String DLX_EXCHANGE = "dlx.exchange";

    @Bean
    public TopicExchange ordersTopicExchange() {
        return new TopicExchange(ORDERS_TOPIC_EXCHANGE, true, false);
    }

    @Bean
    public DirectExchange dlxExchange() {
        return new DirectExchange(DLX_EXCHANGE, true, false);
    }

    @Bean
    public Queue notifQueue() {
        return QueueBuilder.durable(NOTIF_QUEUE)
                .withArgument("x-dead-letter-exchange", DLX_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", NOTIF_QUEUE_DLQ)
                .build();
    }

    @Bean
    public Queue notifQueueDlq() {
        return QueueBuilder.durable(NOTIF_QUEUE_DLQ).build();
    }

    @Bean
    public Binding notifQueueBinding(Queue notifQueue, TopicExchange ordersTopicExchange) {
        return BindingBuilder.bind(notifQueue).to(ordersTopicExchange).with(ORDER_WILDCARD_ROUTING_KEY);
    }

    @Bean
    public Binding notifQueueDlqBinding(Queue notifQueueDlq, DirectExchange dlxExchange) {
        return BindingBuilder.bind(notifQueueDlq).to(dlxExchange).with(NOTIF_QUEUE_DLQ);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory, MessageConverter jsonMessageConverter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jsonMessageConverter);
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        return factory;
    }
}
