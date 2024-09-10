package gr.police.polseal.dto.mapper;

import gr.police.polseal.dto.OuDto;
import gr.police.polseal.model.Ou;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "cdi", uses = {EnumMapper.class}, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface OuMapper {

  OuDto toOuDto(Ou ou);

  List<OuDto> toOuDto(List<Ou> ou);

}
