package net.thumbtack.buscompany.controller;

import lombok.AllArgsConstructor;
import net.thumbtack.buscompany.dto.response.UserDto;
import net.thumbtack.buscompany.exception.BuscompanyException;
import net.thumbtack.buscompany.model.Role;
import net.thumbtack.buscompany.service.AdminService;
import net.thumbtack.buscompany.service.ClientService;
import net.thumbtack.buscompany.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;

@RestController
@AllArgsConstructor
public class UserController {

    private UserService userService;
    private AdminService adminService;
    private ClientService clientService;

    @GetMapping(value = "/api/accounts",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public UserDto userInfo(Authentication authentication) {
        String username = authentication.getName();
        if (authentication.getAuthorities().contains(Role.ROLE_ADMIN)) {
            return adminService.adminInfo(username);
        }
        if (authentication.getAuthorities().contains(Role.ROLE_CLIENT)) {
            return clientService.clientInfo(username);
        }
        return null;
    }

    @DeleteMapping(value = "/api/sessions",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public void logout(HttpServletRequest request, HttpServletResponse httpServletResponse) {
        userService.logout(request);
        httpServletResponse.setStatus(HttpStatus.OK.value());
    }

    @DeleteMapping(value = "/api/accounts")
    public void userLeft(Principal principal) throws BuscompanyException {
        userService.userLeft(principal.getName());
    }
}
