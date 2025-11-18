package nl.fontys.tweetleuserservice.business.service;

import nl.fontys.tweetleuserservice.domain.UserEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import static nl.fontys.tweetleuserservice.domain.RabbitMQConstants.*;

@Service
public class PublishService {

    private final RabbitTemplate rabbitTemplate;

    public PublishService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishUserRegistered(UserEvent event) {
        rabbitTemplate.convertAndSend(
                USER_AUTH_EXCHANGE,
                USER_AUTH_REGISTERED_KEY,
                event
        );
        System.out.println("Published user registration to " + USER_AUTH_EXCHANGE);
    }

    public void publishUserUpdated(UserEvent event) {
        rabbitTemplate.convertAndSend(
                USER_GENERAL_EXCHANGE,
                USER_UPDATED_KEY,
                event
        );
        System.out.println("Published user update to " + USER_GENERAL_EXCHANGE);
    }

    public void publishUserDeleted(UserEvent event) {
        rabbitTemplate.convertAndSend(
                USER_GENERAL_EXCHANGE,
                USER_DELETED_KEY,
                event
        );
        System.out.println("Published user deletion to " + USER_GENERAL_EXCHANGE);
    }
}
