package com.yoga.spendanalyser.user.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class MessageConfiguration {


    @Bean
    public Queue otpQueue() {
        return new Queue("otpQueue", false);
    }

    @Bean
    public DirectExchange userExchange() {
        return new DirectExchange("user.exchange");
    }

    @Bean
    Binding otpBinding(Queue otpQueue, DirectExchange userExchange) {
        return BindingBuilder.bind(otpQueue).to(userExchange).with("user.otp.rk");
    }

    @Bean
    public MessageConverter jsonMessageConvertor() {
        return new Jackson2JsonMessageConverter();
    }

    public AmqpTemplate rabbitTemplate(ConnectionFactory connectionFactory) {

        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConvertor());
        return rabbitTemplate;
    }
}
