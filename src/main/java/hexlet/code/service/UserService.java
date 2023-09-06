package hexlet.code.service;

import hexlet.code.dto.UserDto;
import hexlet.code.model.User;

public interface UserService {
    User getUserById (long id);
    User createNewUser(UserDto userDto);
    User updateUser(UserDto userDto, long id);
}
