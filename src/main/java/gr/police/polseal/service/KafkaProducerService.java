package gr.police.polseal.service;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import gr.police.polseal.dto.SignRequestDto;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

import java.util.Locale;

@ApplicationScoped
public class KafkaProducerService {


    //It seems that @Channel creates an in-memory channel if it not the same as the smallrye channel defined in application.yml
    @Channel("signatures-out")
    Emitter<SignRequestDto> emitter;

    public void sendPrice(SignRequestDto signRequest) {
        emitter.send(signRequest);
    }
}

