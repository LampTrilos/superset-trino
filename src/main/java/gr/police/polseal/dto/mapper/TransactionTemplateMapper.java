package gr.police.polseal.dto.mapper;

import gr.police.polseal.dto.TransactionTemplateReqDto;
import gr.police.polseal.dto.TransactionTemplateResDto;
import gr.police.polseal.model.TransactionTemplate;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

import java.util.List;

//@Mapper(componentModel = "cdi", uses = {EnumMapper.class, ItemCodeMapper.class}, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
@Mapper(componentModel = "cdi", uses = {EnumMapper.class}, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface TransactionTemplateMapper {

  TransactionTemplateResDto toTransactionTemplateDto(TransactionTemplate transactionTemplate);

  List<TransactionTemplateResDto> toTransactionTemplateDto(List<TransactionTemplate> transactionTemplate);

  TransactionTemplate toTransactionTemplate(TransactionTemplateReqDto transactionTemplateReqDto);

}
