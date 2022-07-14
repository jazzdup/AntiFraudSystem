package antifraud;

import antifraud.dto.IP;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface IPRepository extends CrudRepository<IP, Long> {
    Optional<IP> findByIp(String ip);
}
