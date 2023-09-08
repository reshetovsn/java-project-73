package hexlet.code.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.GenerationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import static jakarta.persistence.TemporalType.TIMESTAMP;

import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank(message = "FirstName must not be empty")
    private String firstName;

    @NotBlank(message = "LastName must not be empty")
    private String lastName;

    @Column(unique = true)
    @Email
    private String email;

    @NotBlank
    @Size(min = 3, max = 100, message = "Password must be larger than three characters")
    @JsonIgnore // Чтобы jackson игнорировал поле при сериализации объекта пользователя в JSON строку
    private String password;

    @CreationTimestamp
    @Temporal(TIMESTAMP)
    private Date createdAt;
}
