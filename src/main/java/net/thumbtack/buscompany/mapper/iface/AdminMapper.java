package net.thumbtack.buscompany.mapper.iface;

import net.thumbtack.buscompany.dto.request.AdminProfileEditRequestDto;
import net.thumbtack.buscompany.dto.request.AdminRegisterRequestDto;
import net.thumbtack.buscompany.dto.response.AdminProfileEditResponseDto;
import net.thumbtack.buscompany.dto.response.AdminResponseDto;
import net.thumbtack.buscompany.model.Admin;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AdminMapper {

    @Mapping(source = "firstName", target = "firstName")
    @Mapping(target = "userType", expression = "java(admin.getRole().getUsertype())")
    AdminResponseDto adminDto(Admin admin);

    @Mapping(source = "login", target = "username")
    Admin adminDtoToAdmin(AdminRegisterRequestDto adminRegisterRequestDto);

    @Mapping(target = "userType", expression = "java(admin.getRole().getUsertype())")
    AdminProfileEditResponseDto adminProfileEditDto(Admin admin);

    @Mapping(target = "firstName", source = "requestDto.firstName")
    @Mapping(target = "lastName", source = "requestDto.lastName")
    @Mapping(target = "patronymic", source = "requestDto.patronymic")
    @Mapping(target = "position", source = "requestDto.position")
    @Mapping(target = "password", source = "requestDto.newPassword")
    Admin editAdmin(Admin admin, AdminProfileEditRequestDto requestDto);
}
