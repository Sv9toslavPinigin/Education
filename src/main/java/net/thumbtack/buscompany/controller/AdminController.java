package net.thumbtack.buscompany.controller;


import lombok.AllArgsConstructor;
import net.thumbtack.buscompany.dto.request.AdminProfileEditRequestDto;
import net.thumbtack.buscompany.dto.request.AdminRegisterRequestDto;
import net.thumbtack.buscompany.dto.response.AdminProfileEditResponseDto;
import net.thumbtack.buscompany.dto.response.AdminResponseDto;
import net.thumbtack.buscompany.dto.response.BusInfoDto;
import net.thumbtack.buscompany.dto.response.ClientResponseDto;
import net.thumbtack.buscompany.exception.BuscompanyException;
import net.thumbtack.buscompany.service.AdminService;
import net.thumbtack.buscompany.service.BusService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@AllArgsConstructor
public class AdminController {
    private static final Logger LOGGER = LoggerFactory.getLogger(AdminController.class);
    private AdminService adminService;
    private BusService busService;

    @PostMapping(value = "/api/admins")
    public AdminResponseDto adminRegistration(@Valid @RequestBody AdminRegisterRequestDto requestDto,
                                              HttpServletRequest request) throws BuscompanyException {
        AdminResponseDto responseDto = adminService.registerAdmin(requestDto, request);
        LOGGER.info("Admin registered");
        return responseDto;
    }

    @GetMapping(value = "/api/clients", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ClientResponseDto> getAllClients() {
        return adminService.getAllClients();
    }

    @PutMapping(value = "/api/admins", produces = MediaType.APPLICATION_JSON_VALUE)
    public AdminProfileEditResponseDto editAdminProfile(@RequestBody @Valid AdminProfileEditRequestDto requestDto,
                                                        Authentication authentication) throws BuscompanyException {
        return adminService.editAdminProfile(requestDto, authentication);
    }

    @GetMapping(value = "/api/buses")
    public List<BusInfoDto> busInfo() {
        return busService.getAllBuses();
    }

}
