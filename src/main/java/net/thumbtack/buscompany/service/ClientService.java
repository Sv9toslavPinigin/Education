package net.thumbtack.buscompany.service;

import lombok.AllArgsConstructor;
import net.thumbtack.buscompany.dto.request.ClientProfileEditRequestDto;
import net.thumbtack.buscompany.dto.request.ClientRegisterRequestDto;
import net.thumbtack.buscompany.dto.response.ClientProfileEditResponseDto;
import net.thumbtack.buscompany.dto.response.ClientResponseDto;
import net.thumbtack.buscompany.exception.BuscompanyErrorCode;
import net.thumbtack.buscompany.exception.BuscompanyException;
import net.thumbtack.buscompany.mapper.iface.ClientMapper;
import net.thumbtack.buscompany.model.Client;
import net.thumbtack.buscompany.model.Role;
import net.thumbtack.buscompany.repository.iface.ClientRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;

@Service
@AllArgsConstructor
public class ClientService extends ServiceBase {
    private ClientRepository clientRepository;
    private ClientMapper clientMapper;

    public ClientResponseDto registerClient(ClientRegisterRequestDto requestDto,
                                            HttpServletRequest request) throws BuscompanyException {

        Client client = clientMapper.clientDtoToClient(requestDto);
        client.setRole(Role.ROLE_CLIENT);
        client.setEnabled(true);
        try {
            clientRepository.save(client);
        } catch (DataIntegrityViolationException e) {
            throw new BuscompanyException(BuscompanyErrorCode.LOGIN_ALREADY_EXISTS, "login");
        }
        autoLogin(client.getUsername(), client.getPassword(), request);
        return clientMapper.clientDto(client);
    }

    public ClientProfileEditResponseDto editClientProfile(ClientProfileEditRequestDto requestDto,
                                                          Authentication authentication) throws BuscompanyException {

        String username = authentication.getName();
        Client client = clientRepository.findByUsername(username);
        if (!client.getPassword().equals(requestDto.getOldPassword())) {
            throw new BuscompanyException(BuscompanyErrorCode.INVALID_OLD_PASSWORD, "oldPassword");
        }
        client = clientMapper.editClient(client, requestDto);
        clientRepository.save(client);
        return clientMapper.clientProfileEditDto(client);
    }

    public ClientResponseDto clientInfo(String username) {
        return clientMapper.clientDto(clientRepository.findByUsername(username));
    }
}