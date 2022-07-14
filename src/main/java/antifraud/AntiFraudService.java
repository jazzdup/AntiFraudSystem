package antifraud;

import antifraud.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class AntiFraudService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AntiFraudService.class);
    @Autowired
    IPRepository ipRepository;
    @Autowired
    CardRepository cardRepository;
    @Autowired
    TxRepository txRepository;
    @Autowired
    TxLimitsRepository txLimitsRepository;

    private static final Pattern PATTERN = Pattern.compile(
            "^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");
    public static boolean validateIP(final String ip) {
        return PATTERN.matcher(ip).matches();
    }
    public static boolean checkCardNumber(String s) {
        //convert long to int[]
        int[] digits = new int[s.length()];
        for (int i = 0; i < s.length(); i++) {
            digits[i] = Integer.parseInt(s.substring(i, i + 1));
        }
        int sum = 0;
        int length = digits.length;
        for (int i = 0; i < length; i++) {
            // get digits in reverse order
            int digit = digits[length - i - 1];
            // every 2nd number multiply with 2
            if (i % 2 == 1) {
                digit *= 2;
            }
            sum += digit > 9 ? digit - 9 : digit;
        }
        return sum % 10 == 0;
    }

    public IP addSuspiciousIp(IP ip) throws AssignExistingEntityException, BadRequestException {
        if (ipRepository.findByIp(ip.getIp()).isPresent()) {
            throw new AssignExistingEntityException();
        } else if (! validateIP(ip.getIp())){
            throw new BadRequestException("Invalid IP");
        } else {
            return ipRepository.save(ip);
        }
    }

    public Status deleteSuspiciousIp(String ip) throws BadRequestException {
        if (! validateIP(ip)){
            throw new BadRequestException("Invalid IP");
        }
        IP found = ipRepository.findByIp(ip).orElseThrow(() -> new EntityNotFoundException("IP not found"));
        ipRepository.delete(found);
        return new Status(String.format("IP %s successfully removed!", ip));
    }

    public List<IP> getSuspiciousIp() {
        return (List<IP>) ipRepository.findAll();
    }

    public IdNumber addStolenCard(IdNumber idNumber) throws AssignExistingEntityException, BadRequestException {
        if (cardRepository.findByNumber(idNumber.getNumber()).isPresent()) {
            throw new AssignExistingEntityException();
        } else {
            return cardRepository.save(idNumber);
        }
    }

    public Status deleteStolenCard(String number) throws BadRequestException {
        if ( ! checkCardNumber(number)){
            throw new BadRequestException("Invalid card number");
        }
        IdNumber found = cardRepository.findByNumber(number)
                .orElseThrow(() -> new EntityNotFoundException("Card not found"));
        cardRepository.delete(found);
        return new Status(String.format("Card %s successfully removed!", number));
    }

    public List<IdNumber> getStolenCards() {
        return (List<IdNumber>) cardRepository.findAll();
    }

    public boolean ipBlacklisted(String ip) {
        return ipRepository.findByIp(ip).isPresent();
    }

    public boolean cardBlacklisted(String number) {
        return cardRepository.findByNumber(number).isPresent();
    }

    public Result doChecks(Transaction transaction) {
        TxLimit txLimit = txLimitsRepository.findById(transaction.getNumber()).orElseGet(TxLimit::new);
        LOGGER.warn("txLimit: {}", txLimit);
        Result result = new Result();
        if (transaction.getAmount() <= txLimit.getMaxAllowed()) {
            result.set(Result.Type.ALLOWED, Result.NONE);
        } else if (transaction.getAmount() > txLimit.getMaxAllowed()
                && transaction.getAmount() <= txLimit.getMaxManual()) {
            result.set(Result.Type.MANUAL_PROCESSING, Result.AMOUNT);
        } else if (transaction.getAmount() > txLimit.getMaxManual()) {
            result.set(Result.Type.PROHIBITED, Result.AMOUNT);
        }
        if (ipBlacklisted(transaction.getIp())) {
            result.set(Result.Type.PROHIBITED, Result.IP);
        }
        if (cardBlacklisted(transaction.getNumber())) {
            result.set(Result.Type.PROHIBITED, Result.CARD_NUMBER);
        }

        Date txDate = transaction.getDate();
        Date oneHourBefore = new Date(txDate.getTime() - 3600000);
        List<Transaction> transactions = txRepository.findAllByNumber(transaction.getNumber()).stream()
                .filter(t -> t.getDate().after(oneHourBefore) && t.getDate().before(txDate))
                .collect(Collectors.toList());
        if (transactions.size() > 1) {
            System.out.println(transactions);
        }

        int regionCount = transaction.otherRegionCount(transactions);
        int ipCount = transaction.otherIpCount(transactions);
        if (regionCount == 2){
            result.set(Result.Type.MANUAL_PROCESSING, Result.REGION_CORRELATION);
        }
        if (ipCount ==2){
            result.set(Result.Type.MANUAL_PROCESSING, Result.IP_CORRELATION);
        }
        if (regionCount > 2) {
            result.set(Result.Type.PROHIBITED, Result.REGION_CORRELATION);
        }
        if (ipCount > 2) {
            result.set(Result.Type.PROHIBITED, Result.IP_CORRELATION);
        }
        String info = result.getInfoList().stream().sorted().collect(Collectors.joining(", "));
        result.setInfo(info);
        return result;
    }

    public void saveTransaction(Transaction transaction) {
        txRepository.save(transaction);
        LOGGER.warn("Transaction {} saved", transaction);
    }

    public Transaction updateTransaction(@Valid TransactionFeedback feedback)
            throws AssignExistingEntityException, UnprocessableException {
        Transaction saved = txRepository.findById(feedback.getTransactionId())
                .orElseThrow(() -> new EntityNotFoundException("Transaction not found"));
        if (saved.getFeedback() != null && !saved.getFeedback().isEmpty()) {
            throw new AssignExistingEntityException();
        }

        TxLimit txLimit = txLimitsRepository.findById(saved.getNumber()).orElseGet(() -> new TxLimit(saved.getNumber()));

        if (feedback.getFeedback().equals(saved.getResult())){
            throw new UnprocessableException();
        }else if (saved.getResult().equals(Result.Type.ALLOWED.name())
                && feedback.getFeedback().equals(Result.Type.MANUAL_PROCESSING.name())){
            //lower max allowed
            txLimit.lowerMaxAllowed(saved.getAmount());
        }else if (saved.getResult().equals(Result.Type.ALLOWED.name())
                && feedback.getFeedback().equals(Result.Type.PROHIBITED.name())){
            //lower max allowed, manual
            txLimit.lowerBoth(saved.getAmount());
        }else if (saved.getResult().equals(Result.Type.MANUAL_PROCESSING.name())
                && feedback.getFeedback().equals(Result.Type.ALLOWED.name())){
            //raise max allowed
            txLimit.raiseMaxAllowed(saved.getAmount());
        }else if (saved.getResult().equals(Result.Type.MANUAL_PROCESSING.name())
                && feedback.getFeedback().equals(Result.Type.PROHIBITED.name())){
            //lower max manual
            txLimit.lowerMaxManual(saved.getAmount());
        }else if (saved.getResult().equals(Result.Type.PROHIBITED.name())
                && feedback.getFeedback().equals(Result.Type.ALLOWED.name())){
            //raise max allowed, manual
            txLimit.raiseBoth(saved.getAmount());
        }else if (saved.getResult().equals(Result.Type.PROHIBITED.name())
                && feedback.getFeedback().equals(Result.Type.MANUAL_PROCESSING.name())){
            //lower max manual
            txLimit.raiseMaxManual(saved.getAmount());
        }else{
            throw new RuntimeException("INVALID LOGIC, NEEDS FIX!!!");
        }
        txLimitsRepository.save(txLimit);
        saved.setFeedback(feedback.getFeedback());
        return txRepository.save(saved);
    }

    public List<Transaction> getHistory() {
        List<Transaction> all = (List<Transaction>) txRepository.findAll();
        System.out.println(all);
        return all;
    }

    public List<Transaction> getHistoryByCardNumber(String number) throws BadRequestException {
        if ( ! checkCardNumber(number)){
            throw new BadRequestException("Invalid card number");
        }
        List<Transaction> list = txRepository.findAllByNumber(number);
        if (list.isEmpty()){
            throw new EntityNotFoundException("No transactions found for card number " + number);
        }else{
            return list;
        }
    }
}
