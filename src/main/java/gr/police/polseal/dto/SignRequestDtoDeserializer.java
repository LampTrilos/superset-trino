package gr.police.polseal.dto;

import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;

//This class deserializes the Kafka Messages into SignRequestsDto
public class SignRequestDtoDeserializer extends ObjectMapperDeserializer<SignRequestDto> {
    public SignRequestDtoDeserializer() {
        super(SignRequestDto.class);
    }

}
