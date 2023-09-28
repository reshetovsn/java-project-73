package hexlet.code.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    @NotBlank(message = "FirstName must not be empty")
    private String firstName;

    @NotBlank(message = "LastName must not be empty")
    private String lastName;

    @Email
    private String email;

    @NotBlank
    @Size(min = 3, max = 100, message = "Password must be larger than three characters")
    private String password;
}
