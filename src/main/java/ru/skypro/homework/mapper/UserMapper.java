package ru.skypro.homework.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;
import ru.skypro.homework.dto.RegisterReq;
import ru.skypro.homework.dto.User;
import ru.skypro.homework.dto.UserSecurity;
import ru.skypro.homework.entity.UserEntity;

@Component
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    @Mapping(target = "image", defaultValue = "null")
    User toDto(UserEntity userEntity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", defaultValue = "USER")
    @Mapping(target = "image", defaultValue = "null")
    UserEntity toEntity(User userDto);

    @Mapping(target = "email", source = "username")
    UserEntity toEntityFromReq(RegisterReq registerReqDto);

    UserSecurity toSecurityDTO(UserEntity userEntity);
}
