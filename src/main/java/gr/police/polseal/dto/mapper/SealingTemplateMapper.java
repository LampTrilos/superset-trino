package gr.police.polseal.dto.mapper;

import gr.police.polseal.dto.SealingApplicationDto;
import gr.police.polseal.dto.SealingTemplateDto;
import gr.police.polseal.model.SealingTemplate;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "cdi", uses = {EnumMapper.class}, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface SealingTemplateMapper {

  SealingTemplateDto toSealingTemplateDto(SealingTemplate sealingTemplate);

  List<SealingApplicationDto> toSealingTemplateDto(List<SealingTemplate> sealingTemplate);

}
