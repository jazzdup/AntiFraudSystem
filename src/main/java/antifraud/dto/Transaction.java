package antifraud.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.LuhnCheck;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.Date;
import java.util.List;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

@Getter
@Setter
@NoArgsConstructor
@Entity
@ToString
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    @JsonProperty("transactionId")
    private Long id;

    @Min(1)
    long amount;
    @NotBlank
    String ip;
    @NotBlank
    @LuhnCheck
    String number;
    @NotBlank
    String region;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    Date date;
    String result;
    @Pattern(regexp = "ALLOWED|MANUAL_PROCESSING|PROHIBITED")
    String feedback;
    @JsonProperty("feedback")
    public String getFeedback() {
        return feedback == null ? "" : feedback;
    }

    public int otherRegionCount(List<Transaction> transactions) {
        return transactions.stream()
                .filter(t -> ! t.getRegion().equals(this.region))
                .collect(groupingBy(Transaction::getRegion, counting())).size();
    }
    public int otherIpCount(List<Transaction> transactions) {
        return transactions.stream()
                .filter(t -> ! t.getIp().equals(ip))
                .collect(groupingBy(Transaction::getIp, counting())).size();
    }


}
