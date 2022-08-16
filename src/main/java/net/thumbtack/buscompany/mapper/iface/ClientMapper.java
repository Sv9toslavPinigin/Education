package net.thumbtack.buscompany.mapper.iface;

import net.thumbtack.buscompany.dto.request.ClientProfileEditRequestDto;
import net.thumbtack.buscompany.dto.request.ClientRegisterRequestDto;
import net.thumbtack.buscompany.dto.response.ClientProfileEditResponseDto;
import net.thumbtack.buscompany.dto.response.ClientResponseDto;
import net.thumbtack.buscompany.model.Client;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ClientMapper {


    @Mapping(source = "firstName", target = "firstName")
    @Mapping(target = "userType", expression = "java(client.getRole().getUsertype())")
    ClientResponseDto clientDto(Client client);

    @Mapping(source = "firstName", target = "firstName")
    @Mapping(source = "login", target = "username")
    Client clientDtoToClient(ClientRegisterRequestDto clientRegisterRequestDto);

    @Mapping(target = "userType", expression = "java(client.getRole().getUsertype())")
    ClientProfileEditResponseDto clientProfileEditDto(Client client);

    @Mapping(target = "firstName", source = "requestDto.firstName")
    @Mapping(target = "lastName", source = "requestDto.lastName")
    @Mapping(target = "patronymic", source = "requestDto.patronymic")
    @Mapping(target = "phone", source = "requestDto.phone")
    @Mapping(target = "email", source = "requestDto.mail")
    @Mapping(target = "password", source = "requestDto.newPassword")
    Client editClient(Client client, ClientProfileEditRequestDto requestDto);
}
