package gr.police.polseal.dto.mapper;

import gr.police.polseal.dto.ChallengeChannelDto;
import gr.police.polseal.dto.ChallengeChannelDto;
import gr.police.polseal.model.ChallengeChannel;
import gr.police.polseal.model.SealingApplication;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "cdi", uses = {EnumMapper.class}, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ChallengeChannelMapper {

  ChallengeChannelDto toChallengeChannelDto(ChallengeChannel challengeChannel);

  List<ChallengeChannelDto> toChallengeChannelDto(List<ChallengeChannel> challengeChannel);

}
