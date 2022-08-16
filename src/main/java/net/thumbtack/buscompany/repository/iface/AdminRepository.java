package net.thumbtack.buscompany.repository.iface;

import net.thumbtack.buscompany.model.Admin;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdminRepository extends CrudRepository<Admin, Integer> {
    List<Admin> findAll();

    Admin findByUsername(String username);

}
