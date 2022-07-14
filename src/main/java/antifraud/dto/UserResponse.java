package antifraud.dto;

import antifraud.UserEntity;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserResponse {
    private long id;
    private String name;
    private String username;
    private String role;

//    public UserResponse(UserDetailsImpl userDetailsImpl) {
//        this.id = userDetailsImpl.getId();
//        this.name = userDetailsImpl.getName();
//        this.username = userDetailsImpl.getUsername();
//        this.role = userDetailsImpl.getRolesAndAuthorities().get(0).getAuthority();
//    }

    public UserResponse(UserEntity userEntity) {
        this.id = userEntity.getId();
        this.name = userEntity.getName();
        this.username = userEntity.getUsername();
        this.role = userEntity.getRole().substring(5);
    }
}
