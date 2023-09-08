package hexlet.code.config.security;

import hexlet.code.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class PersonDetails implements UserDetails {

    private final User user;

    public PersonDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return this.user.getPassword();
    }

    @Override
    public String getUsername() {
        return this.user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() { //этот аккаунт не просрочен
        return true;
    }

    @Override
    public boolean isAccountNonLocked() { //этот аккаунт не заблокирован
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() { //этот пароль не просрочен
        return true;
    }

    @Override
    public boolean isEnabled() { //этот аккаунт работает
        return true;
    }

    public User getUser() { // Нужен, чтобы получать данные аутентифицированного пользователя
        return this.user;
    }
}
