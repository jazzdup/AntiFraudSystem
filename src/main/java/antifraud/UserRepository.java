package antifraud;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;
import java.util.Optional;

public interface UserRepository extends CrudRepository<UserEntity, Long> {
    Optional<UserEntity> findByUsernameIgnoreCase(String toUpperCase);

//    @Transactional
//    @Query("UPDATE UserEntity u SET u.isNonLocked = ?1 WHERE u.username = ?2")
//    @Modifying
//    void updateLock(boolean isNonLocked, String username);
}
