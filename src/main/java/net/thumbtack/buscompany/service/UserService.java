package net.thumbtack.buscompany.service;

import net.thumbtack.buscompany.exception.BuscompanyErrorCode;
import net.thumbtack.buscompany.exception.BuscompanyException;
import net.thumbtack.buscompany.model.Role;
import net.thumbtack.buscompany.model.User;
import net.thumbtack.buscompany.repository.iface.AdminRepository;
import net.thumbtack.buscompany.repository.iface.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
public class UserService implements UserDetailsService {

    private UserRepository userRepository;

    public UserService(UserRepository userRepository, AdminRepository adminRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return user;
    }

    public void logout(HttpServletRequest request) {
        SecurityContextHolder.getContext().getAuthentication().setAuthenticated(false);
        request.getSession().invalidate();
    }

    public void userLeft(String username) throws BuscompanyException {
        User user = userRepository.findByUsername(username);
        if (user.getAuthorities().contains(Role.ROLE_ADMIN)
                && userRepository.countByRoleAndEnabledTrue(Role.ROLE_ADMIN) < 2) {
            throw new BuscompanyException(BuscompanyErrorCode.LAST_ADMIN, "login");
        }
        user.setEnabled(false);
        userRepository.save(user);
    }
}
