package antifraud.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
public class UsernameOperation {
    @NotBlank
    private String username;
    @NotBlank
    private String operation;
}
