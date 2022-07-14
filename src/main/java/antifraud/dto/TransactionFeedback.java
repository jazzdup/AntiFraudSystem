package antifraud.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@NoArgsConstructor
public class TransactionFeedback {
    @Min(1)
    Long transactionId;
    @NotBlank
    @Pattern(regexp = "ALLOWED|MANUAL_PROCESSING|PROHIBITED")
    String feedback;
}
