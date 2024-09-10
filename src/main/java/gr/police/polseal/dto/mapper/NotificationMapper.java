package gr.police.polseal.dto.mapper;

import gr.police.polseal.dto.NotificationDto;
import gr.police.polseal.model.Notification;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "cdi", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface NotificationMapper {

  NotificationDto toNotificationDto(Notification notification);

  List<NotificationDto> toNotificationDto(List<Notification> notification);

}