package antifraud;

import antifraud.dto.Transaction;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

interface TxRepository  extends CrudRepository<Transaction, Long> {
    List<Transaction> findAllByNumber(String number);
}
