package antifraud;

import antifraud.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
public class AntiFraudController {
    private static final Logger LOGGER = LoggerFactory.getLogger(AntiFraudController.class);
    @Autowired
    private AntiFraudService antiFraudService;

    @PostMapping("/api/antifraud/transaction")
    public Result postTransaction(@Valid @RequestBody Transaction transaction) {
        LOGGER.debug("Received transaction: {}", transaction);
        Result result = antiFraudService.doChecks(transaction);
        transaction.setResult(result.getResult());
        antiFraudService.saveTransaction(transaction);
        return result;
    }

    @PutMapping("/api/antifraud/transaction")
    public Transaction putTransaction(@Valid @RequestBody TransactionFeedback transactionFeedback)
            throws AssignExistingEntityException, UnprocessableException {
        LOGGER.debug("Received transactionFeedback: {}", transactionFeedback);
        return antiFraudService.updateTransaction(transactionFeedback);
    }

    @PostMapping("/api/antifraud/suspicious-ip")
    public IP postSuspiciousIp(@Valid @RequestBody IP ip) throws AssignExistingEntityException, BadRequestException {
        LOGGER.debug("Add suspicious ip: {}", ip.getIp());
        return antiFraudService.addSuspiciousIp(ip);
    }

    @DeleteMapping("/api/antifraud/suspicious-ip/{ip}")
    public Status deleteSuspiciousIp(@PathVariable String ip) throws BadRequestException {
        LOGGER.debug("Delete suspicious ip: {}", ip);
        return antiFraudService.deleteSuspiciousIp(ip);
    }

    @GetMapping("/api/antifraud/suspicious-ip")
    public List<IP> getSuspiciousIp() {
        LOGGER.debug("Get suspicious ip");
        return antiFraudService.getSuspiciousIp();
    }

    @PostMapping("/api/antifraud/stolencard")
    public IdNumber postStolenCard(@Valid @RequestBody IdNumber idNumber)
            throws AssignExistingEntityException, BadRequestException {
        LOGGER.debug("Stolen card number: {}", idNumber.getNumber());
        return antiFraudService.addStolenCard(idNumber);
    }

    @DeleteMapping("/api/antifraud/stolencard/{number}")
    public Status deleteStolenCard(@PathVariable("number") String number) throws BadRequestException {
        LOGGER.debug("Delete stolen card number: {}", number);
        return antiFraudService.deleteStolenCard(number);
    }

    @GetMapping("/api/antifraud/stolencard")
    public List<IdNumber> getStolenCards() {
        LOGGER.debug("Get stolen cards");
        return antiFraudService.getStolenCards();
    }

    @GetMapping("/api/antifraud/history")
    public List<Transaction> getHistory() {
        LOGGER.debug("Get history");
        return antiFraudService.getHistory();
    }
    @GetMapping("/api/antifraud/history/{number}")
    public List<Transaction> getHistoryByCardNumber(@PathVariable("number") String number) throws BadRequestException {
        LOGGER.debug("Get history by card number: {}", number);
        return antiFraudService.getHistoryByCardNumber(number);
    }
}
