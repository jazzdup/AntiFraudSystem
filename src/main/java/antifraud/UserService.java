package antifraud;

import antifraud.dto.UsernameOperation;
import antifraud.dto.UsernameRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService implements UserDetailsService{
    @Autowired
    private UserRepository userRepository;
    @Bean
    public PasswordEncoder getEncoder() {
        return new BCryptPasswordEncoder();
    }

    public UserEntity register(User user) throws RegisterUserExistsException {
        if (userRepository.findByUsernameIgnoreCase(user.getUsername()).isPresent()) {
            throw new RegisterUserExistsException();
        }else{
            UserEntity userToSave = new UserEntity(user);
            userToSave.setPassword(getEncoder().encode(user.getPassword()));
            //save ALL as ROLE_MERCHANT, but if it's id=1, update to ROLE_ADMIN
            userToSave.setRole(User.ROLE_MERCHANT);
            userToSave.setLocked(true);
            UserEntity saved = userRepository.save(userToSave);
            if (saved.getId() == 1) {
                saved.setRole(User.ROLE_ADMINISTRATOR);
                saved.setLocked(false);
                userRepository.save(saved);
            }
            return saved;
        }
    }

    public List<UserEntity> list() {
        return (List<UserEntity>) userRepository.findAll();
    }

    public void delete(String username) {
        UserEntity userEntity = userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow( () -> new UsernameNotFoundException("User not found"));
        userRepository.delete(userEntity);
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow( () -> new UsernameNotFoundException("User not found"));
        return new User(userEntity);
    }

    public UserEntity updateRole(UsernameRole usernameRole) throws BadRequestException, AssignExistingEntityException {
        if ( ! usernameRole.getRole().equals("MERCHANT")
                && ! usernameRole.getRole().equals("SUPPORT")) {
            throw new BadRequestException("Invalid role specified");
        }
        UserEntity userEntity = userRepository.findByUsernameIgnoreCase(usernameRole.getUsername())
                .orElseThrow( () -> new UsernameNotFoundException("User not found"));
        if (userEntity.getRole().equals("ROLE_" + usernameRole.getRole())) {
            throw new AssignExistingEntityException();
        }
        userEntity.setRole("ROLE_" + usernameRole.getRole());
        return userRepository.save(userEntity);
    }

    public String updateAccess(UsernameOperation usernameOperation) throws BadRequestException {
        UserEntity userEntity = userRepository.findByUsernameIgnoreCase(usernameOperation.getUsername())
                .orElseThrow( () -> new UsernameNotFoundException("User not found"));
        if (userEntity.getRole().equals(User.ROLE_ADMINISTRATOR)){
            throw new BadRequestException("Cannot change access of admin");
        }
        if (usernameOperation.getOperation().equals("LOCK")) {
            userEntity.setLocked(true);
        }else if (usernameOperation.getOperation().equals("UNLOCK")) {
            userEntity.setLocked(false);
        }else{
            throw new BadRequestException("Invalid operation specified");
        }
        userRepository.save(userEntity);
        String status = String.format("User %s %s!", userEntity.getUsername(),
                userEntity.isLocked() ? "locked" : "unlocked");
        return status;
    }
}
