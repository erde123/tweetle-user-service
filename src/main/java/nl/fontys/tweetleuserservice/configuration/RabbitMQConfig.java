package nl.fontys.tweetleuserservice.configuration;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static nl.fontys.tweetleuserservice.domain.RabbitMQConstants.*;


@Configuration
public class RabbitMQConfig {


    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
        converter.setAlwaysConvertToInferredType(true);
        return converter;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            Jackson2JsonMessageConverter messageConverter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);
        return factory;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         Jackson2JsonMessageConverter messageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        return template;
    }

    @Bean
    public TopicExchange userAuthExchange() {
        return new TopicExchange(USER_AUTH_EXCHANGE);
    }

    @Bean
    public TopicExchange userGeneralExchange() {
        return new TopicExchange(USER_GENERAL_EXCHANGE);
    }

    @Bean
    public Queue authUserRegisterQueue() {
        return new Queue("auth.user.register.queue", true);
    }

    @Bean
    public Queue authUserUpdateUsernameQueue() {
        return new Queue("auth.user.update.username.queue", true);
    }

    @Bean
    public Queue authUserDeleteQueue() {
        return new Queue("auth.user.delete.queue", true);
    }

    @Bean
    public Binding bindAuthRegisterQueue() {
        return BindingBuilder.bind(authUserRegisterQueue())
                .to(userAuthExchange())
                .with(USER_AUTH_REGISTERED_KEY);

    }

    @Bean
    public Binding bindAuthUpdateQueue() {
        return BindingBuilder.bind(authUserUpdateUsernameQueue())
                .to(userGeneralExchange())
                .with(USER_UPDATED_KEY);
    }

    @Bean
    public Binding bindAuthDeleteQueue() {
        return BindingBuilder.bind(authUserDeleteQueue())
                .to(userGeneralExchange())
                .with(USER_DELETED_KEY);
    }
}