package hexlet.code.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @Column(unique = true)
    @Email
    private String email;

    @NotBlank
    @Size(min = 3, max = 100)
    @JsonIgnore // Чтобы jackson игнорировал поле при сериализации объекта пользователя в JSON строку
    private String password;

    @CreationTimestamp
    @Temporal(TIMESTAMP)
    private Date createdAt;
}
