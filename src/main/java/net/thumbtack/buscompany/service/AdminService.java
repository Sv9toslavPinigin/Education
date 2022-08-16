package net.thumbtack.buscompany.service;

import lombok.AllArgsConstructor;
import net.thumbtack.buscompany.dto.request.AdminProfileEditRequestDto;
import net.thumbtack.buscompany.dto.request.AdminRegisterRequestDto;
import net.thumbtack.buscompany.dto.response.AdminProfileEditResponseDto;
import net.thumbtack.buscompany.dto.response.AdminResponseDto;
import net.thumbtack.buscompany.dto.response.ClientResponseDto;
import net.thumbtack.buscompany.exception.BuscompanyErrorCode;
import net.thumbtack.buscompany.exception.BuscompanyException;
import net.thumbtack.buscompany.mapper.iface.AdminMapper;
import net.thumbtack.buscompany.mapper.iface.ClientMapper;
import net.thumbtack.buscompany.model.Admin;
import net.thumbtack.buscompany.model.Role;
import net.thumbtack.buscompany.repository.iface.AdminRepository;
import net.thumbtack.buscompany.repository.iface.ClientRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AdminService extends ServiceBase {
    private AdminRepository adminRepository;
    private ClientRepository clientRepository;
    private AdminMapper adminMapper;
    private ClientMapper clientMapper;


    public AdminResponseDto registerAdmin(AdminRegisterRequestDto requestDto,
                                          HttpServletRequest request) throws BuscompanyException {

        Admin admin = adminMapper.adminDtoToAdmin(requestDto);
        admin.setRole(Role.ROLE_ADMIN);
        admin.setEnabled(true);

        try {
            adminRepository.save(admin);
        } catch (DataIntegrityViolationException e) {
            throw new BuscompanyException(BuscompanyErrorCode.LOGIN_ALREADY_EXISTS, "login");
        }
        autoLogin(admin.getUsername(), admin.getPassword(), request);
        return adminMapper.adminDto(admin);
    }

    public AdminResponseDto adminInfo(String username) {
        Admin admin = adminRepository.findByUsername(username);
        return adminMapper.adminDto(admin);
    }

    public AdminProfileEditResponseDto editAdminProfile(AdminProfileEditRequestDto requestDto,
                                                        Authentication authentication) throws BuscompanyException {
        String username = authentication.getName();
        Admin admin = adminRepository.findByUsername(username);
        if (!admin.getPassword().equals(requestDto.getOldPassword())) {
            throw new BuscompanyException(BuscompanyErrorCode.INVALID_OLD_PASSWORD, "oldPassword");
        }
        admin = adminMapper.editAdmin(admin, requestDto);
        adminRepository.save(admin);
        return adminMapper.adminProfileEditDto(admin);
    }

    public List<ClientResponseDto> getAllClients() {
        return clientRepository.findAll().stream()
                .map(clientMapper::clientDto)
                .collect(Collectors.toList());
    }
}
