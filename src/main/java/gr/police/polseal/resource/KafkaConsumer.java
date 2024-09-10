package gr.police.polseal.resource;

import gr.police.polseal.dto.SignRequestDto;
import io.smallrye.reactive.messaging.kafka.api.IncomingKafkaRecordMetadata;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;

import javax.enterprise.context.ApplicationScoped;
import java.util.concurrent.CompletionStage;

@ApplicationScoped
public class KafkaConsumer {

    //Doesnt work as it isnt a constant :(
//    @ConfigProperty(name = "KAFKA_CHANNEL")
//    String kafkaChannel;

    //Consumes events from Kafka queue and actually does the signing
    @Incoming("signatures-in")
    public CompletionStage<Void> consume(Message<SignRequestDto> message) {
        // access record metadata
        //IncomingKafkaRecordMetadata metadata = msg.getMetadata(IncomingKafkaRecordMetadata.class).orElseThrow();
        // process the message payload.
        SignRequestDto incomingSignRequest = message.getPayload();
        System.out.println("User: " + incomingSignRequest.getUserId() + " requested to sign the template with Code: " + incomingSignRequest.getTemplateCode());
        // Acknowledge the incoming message (commit the offset)
        return message.ack();
    }

}
