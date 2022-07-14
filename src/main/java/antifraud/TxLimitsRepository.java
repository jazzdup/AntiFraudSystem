package antifraud;

import antifraud.dto.Transaction;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

interface TxLimitsRepository extends CrudRepository<TxLimit, String> {
}
