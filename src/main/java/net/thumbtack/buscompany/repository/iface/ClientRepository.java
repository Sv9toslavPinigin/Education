package net.thumbtack.buscompany.repository.iface;

import net.thumbtack.buscompany.model.Client;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClientRepository extends CrudRepository<Client, Integer> {
    List<Client> findAll();

    Client findByUsername(String username);

}
