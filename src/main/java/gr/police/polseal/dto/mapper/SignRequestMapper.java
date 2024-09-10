package gr.police.polseal.dto.mapper;

import gr.police.polseal.dto.SignRequestDto;
import gr.police.polseal.model.SignRequest;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "cdi", uses = {EnumMapper.class}, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface SignRequestMapper {

  SignRequestDto toSignRequestDto(SignRequest signRequest);

  List<SignRequestDto> toSignRequestDto(List<SignRequest> signRequest);

  SignRequest toSignRequest(SignRequestDto signRequestDto);

  List<SignRequest> toSignRequest(List<SignRequestDto> signRequestDtos);

}
