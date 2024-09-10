package gr.police.polseal.dto.mapper;

import eu.ubitech.bitt.core.domain.auth.model.entity.KeycloakUser;
import gr.police.polseal.dto.SignUpRequest;
import gr.police.polseal.dto.UserDto;
import gr.police.polseal.dto.UserRoleDto;
import gr.police.polseal.model.User;
import gr.police.polseal.model.permissions.UserRole;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "cdi", uses = {EnumMapper.class}, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface UserMapper {

  User toUser(SignUpRequest signUpRequest, String realm);

  UserDto toUserDto(User user);

  User toUser(UserDto userDto);

  List<UserDto> toUserDto(List<User> users);

  UserRoleDto toUserRoleDto(UserRole userRole);

  List<UserRoleDto> toUserRoleDto(List<UserRole> userRoles);

  KeycloakUser toKeycloakUser(User user);

}
