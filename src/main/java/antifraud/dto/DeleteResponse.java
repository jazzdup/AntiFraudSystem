package antifraud.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DeleteResponse {
    private String username;
    private String status;
    public DeleteResponse(String username, String status) {
        this.username = username;
        this.status = status;
    }
}
