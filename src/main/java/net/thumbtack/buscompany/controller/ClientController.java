package net.thumbtack.buscompany.controller;

import lombok.AllArgsConstructor;
import net.thumbtack.buscompany.dto.request.ClientProfileEditRequestDto;
import net.thumbtack.buscompany.dto.request.ClientRegisterRequestDto;
import net.thumbtack.buscompany.dto.response.ClientProfileEditResponseDto;
import net.thumbtack.buscompany.dto.response.ClientResponseDto;
import net.thumbtack.buscompany.exception.BuscompanyException;
import net.thumbtack.buscompany.service.ClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@AllArgsConstructor
public class ClientController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientController.class);
    private ClientService clientService;

    @PostMapping(value = "/api/clients",
            headers = {"Content-type=application/json"})
    public ClientResponseDto clientRegistration(@Valid @RequestBody ClientRegisterRequestDto requestDto,
                                                HttpServletRequest request) throws BuscompanyException {
        ClientResponseDto responseDto = clientService.registerClient(requestDto, request);
        LOGGER.info("Client registered");
        return responseDto;
    }

    @PutMapping(value = "api/clients", produces = MediaType.APPLICATION_JSON_VALUE)
    public ClientProfileEditResponseDto editClientProfile(@RequestBody ClientProfileEditRequestDto requestDto,
                                                          Authentication authentication) throws BuscompanyException {
        return clientService.editClientProfile(requestDto, authentication);
    }
}
