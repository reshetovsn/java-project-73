package hexlet.code.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginDto {

    @NotBlank(message = "FirstName must not be empty")
    private String username;

    @NotBlank
    @Size(min = 3, max = 100, message = "Password must be larger than three characters")
    @JsonIgnore
    private String password;
}
