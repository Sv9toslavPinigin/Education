package net.thumbtack.buscompany.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "clients")
@Getter
@Setter
@NoArgsConstructor
public class Client extends User {
    private String email;
    private String phone;

    @Builder
    public Client(int id, String username, String password, String firstName, String lastName, String patronymic, Role role, boolean enabled, String email, String phone) {
        super(id, username, password, firstName, lastName, patronymic, role, enabled);
        this.email = email;
        setPhone(phone);
    }

    public void setPhone(String phone) {
        if (phone != null) {
            this.phone = phone.replaceAll("-", "");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Client client = (Client) o;
        return email.equals(client.email) && phone.equals(client.phone);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), email, phone);
    }
}
