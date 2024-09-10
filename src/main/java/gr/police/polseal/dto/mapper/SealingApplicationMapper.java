package gr.police.polseal.dto.mapper;

import gr.police.polseal.dto.SealingApplicationDto;
import gr.police.polseal.model.SealingApplication;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "cdi", uses = {EnumMapper.class}, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface SealingApplicationMapper {

  SealingApplicationDto toSealingApplicationDto(SealingApplication sealingApplication);

  List<SealingApplicationDto> toSealingApplicationDto(List<SealingApplication> sealingApplication);

}
