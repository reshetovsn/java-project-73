package hexlet.code.controller;

import com.rollbar.notifier.Rollbar;
import hexlet.code.dto.UserDto;
import hexlet.code.model.User;
import hexlet.code.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.List;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static hexlet.code.controller.UserController.USER_CONTROLLER_PATH;
import static org.springframework.http.HttpStatus.CREATED;
@AllArgsConstructor
@RestController
@RequestMapping("${base-url}" + USER_CONTROLLER_PATH)
public class UserController {
    public static final String USER_CONTROLLER_PATH = "/users";
    public static final String ID = "/{id}";
    private static final String ONLY_OWNER_BY_ID = """
            @userRepository.findById(#id).get().getEmail() == authentication.getName()
        """;
    private final UserService userService;
    @Autowired
    Rollbar rollbar;

    @Operation(summary = "Create a new user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "User created",
                content = @Content(schema = @Schema(implementation = User.class))),
        @ApiResponse(responseCode = "422", description = "Cannot create user with this data",
                content = @Content(schema = @Schema(implementation = User.class)))
    })
    @PostMapping
    @ResponseStatus(CREATED)
    public User registerNew(@RequestBody @Valid final UserDto dto) {
        rollbar.debug("Here is some debug message");
        return userService.createNewUser(dto);
    }

    @Operation(summary = "Get all users")
    // Content используется для указания содержимого ответа
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Users found",
                content = @Content(schema = @Schema(implementation = User.class))), // Указываем тип содержимого ответа
        @ApiResponse(responseCode = "404", description = "Users not found",
                content = @Content(schema = @Schema(implementation = User.class)))
    })
    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @Operation(summary = "Get user by id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User found",
                content = @Content(schema = @Schema(implementation = User.class))),
        @ApiResponse(responseCode = "404", description = "User not found",
                content = @Content(schema = @Schema(implementation = User.class)))
    })
    @GetMapping(ID)
    public User getUserById(@PathVariable final Long id) {
        return userService.getUserById(id);
    }

    @Operation(summary = "Update user by id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User updated",
                content = @Content(schema = @Schema(implementation = User.class))),
        @ApiResponse(responseCode = "422", description = "Cannot update user with this data",
                content = @Content(schema = @Schema(implementation = User.class))),
        @ApiResponse(responseCode = "404", description = "User not found",
                content = @Content(schema = @Schema(implementation = User.class)))
    })
    @PutMapping(ID)
    @PreAuthorize(ONLY_OWNER_BY_ID)
    public User updateUser(@PathVariable final long id, @RequestBody @Valid final UserDto dto) {
        return userService.updateUser(id, dto);
    }

    @Operation(summary = "Delete user by id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User deleted",
                content = @Content(schema =  @Schema(implementation = User.class))),
        @ApiResponse(responseCode = "404", description = "User not found",
                content = @Content(schema = @Schema(implementation = User.class)))
    })
    @DeleteMapping(ID)
    @PreAuthorize(ONLY_OWNER_BY_ID)
    public void deleteUser(@PathVariable final long id) {
        userService.deleteUser(id);
    }
}
