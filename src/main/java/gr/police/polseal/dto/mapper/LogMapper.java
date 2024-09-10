package gr.police.polseal.dto.mapper;

import gr.police.polseal.dto.LogItemDto;
import gr.police.polseal.dto.LogTransactionDto;
import gr.police.polseal.model.logs.LogItem;
import gr.police.polseal.model.logs.LogSyndesiKinitou;
import gr.police.polseal.model.logs.LogTransaction;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "cdi", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface LogMapper {

  LogTransactionDto toLogTransactionDto(LogTransaction logTransaction);

  List<LogTransactionDto> toLogTransactionDto(List<LogTransaction> logTransaction);

  LogItemDto toLogItemDto(LogItem logItem);

  List<LogItemDto> toLogItemDto(List<LogItem> logItem);

  LogItemDto toLogSyndesiKinitouDto(LogSyndesiKinitou logItem);

  List<LogItemDto> toLogSyndesiKinitouDto(List<LogSyndesiKinitou> logItem);

}