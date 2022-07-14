package antifraud.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Status {
    String status;

    public Status(String status) {
        this.status = status;
    }
}
