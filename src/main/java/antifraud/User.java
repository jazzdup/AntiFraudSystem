package antifraud;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * used for request and internally
 */
@Data
@NoArgsConstructor
public class User implements UserDetails {
//    @Pattern(regexp = ".+@.+\\..+")
    @NotBlank
    private String username;
    @NotBlank
    private String password;
    @NotBlank
    private String name;

    private boolean locked;

    @JsonIgnore
    private List<GrantedAuthority> rolesAndAuthorities = new ArrayList<>();

    private long id;

    public User(UserEntity userEntity) {
        username = userEntity.getUsername();
        password = userEntity.getPassword();
        name = userEntity.getName();
        rolesAndAuthorities = List.of(new SimpleGrantedAuthority(userEntity.getRole()));
        id = userEntity.getId();
        locked = userEntity.isLocked();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return rolesAndAuthorities;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return ! locked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    //too much of a faff to use, having tried it:
//    enum Role {
//        ADMINISTATOR("ROLE_ADMINISTRATOR"),
//        USER("ROLE_USER"),
//        SUPPORT("ROLE_SUPPORT"),
//        MERCHANT("ROLE_MERCHANT");
//
//        private Role(String desc) {
//            this.desc = desc;
//        }
//        private String desc;
//        public String toString() {
//            return desc;
//        }
//    }
    public static final String ROLE_MERCHANT = "ROLE_MERCHANT";
    public static final String ROLE_ADMINISTRATOR = "ROLE_ADMINISTRATOR";
    public static final String ROLE_USER = "ROLE_USER";
    public static final String ROLE_SUPPORT = "ROLE_SUPPORT";
    public static final String MERCHANT = "MERCHANT";
    public static final String ADMINISTRATOR = "ADMINISTRATOR";
    public static final String USER = "USER";
    public static final String SUPPORT = "SUPPORT";
}
