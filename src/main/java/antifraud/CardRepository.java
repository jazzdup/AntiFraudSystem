package antifraud;

import antifraud.dto.IdNumber;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface CardRepository extends CrudRepository<IdNumber, Long> {
    Optional<IdNumber> findByNumber(String number);
}
