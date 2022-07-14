package antifraud;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;
    private String name;
    private String username;
    private String password;
    private String role; // should be prefixed with ROLE_
    private boolean locked;

    public UserEntity(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.locked = user.isLocked();
//        this.role = userDetailsImpl.getRolesAndAuthorities().get(0).getAuthority();
    }
}
