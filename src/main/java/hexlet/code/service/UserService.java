package hexlet.code.service;

import hexlet.code.dto.UserDto;
import hexlet.code.model.User;

import java.util.List;

public interface UserService {
    List<User> getAllUsers();
    User getUserById(long id);
    User createNewUser(UserDto userDto);
    User updateUser(long id, UserDto userDto);
    void deleteUser(long id);
    User getCurrentUser();
}
