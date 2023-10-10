package hexlet.code.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Getter
@Setter
@EqualsAndHashCode(of = {"id"})
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(unique = true)
    @Email
    private String email;

    @NotBlank
    @Size(min = 1, message = "Имя должно быть не менее 1 символа")
    private String firstName;

    @NotBlank
    @Size(min = 1, message = "Фамилия должна быть не менее 1 символа")
    private String lastName;

    @JsonIgnore
    @NotBlank
    @Size(min = 3, max = 255, message = "Пароль должен быть от 3 до 255 символов")
    private String password;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Instant createdAt;
}
