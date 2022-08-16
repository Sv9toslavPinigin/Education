package net.thumbtack.buscompany.repository.iface;

import net.thumbtack.buscompany.model.Role;
import net.thumbtack.buscompany.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {
    User findByUsername(String username);

    int countByRoleAndEnabledTrue(Role role);

    boolean existsByUsername(String username);
}
