package antifraud;

import antifraud.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Validated
public class UserController {
    @Autowired
    UserDetailsService userDetailsService;

    @Autowired
    private UserService userService;

    @PostMapping("/api/auth/user")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody User user)
            throws RegisterUserExistsException {
        UserResponse userResponse = new UserResponse(userService.register(user));
        return ResponseEntity.status(HttpStatus.CREATED).body(userResponse);
    }

    @GetMapping("/api/auth/list")
    public ResponseEntity<List<UserResponse>> list() {
        List<UserResponse> userResponses = userService.list().stream()
                .map(user -> new UserResponse(user)).collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(userResponses);
    }

    @PutMapping("/api/auth/role")
    public ResponseEntity<UserResponse> updateRole(@Valid @RequestBody UsernameRole usernameRole)
            throws AssignExistingEntityException, BadRequestException {
        UserResponse userResponse = new UserResponse(userService.updateRole(usernameRole));
        return ResponseEntity.status(HttpStatus.OK).body(userResponse);
    }

    @PutMapping("/api/auth/access")
    public ResponseEntity<Status> updateAccess(@Valid @RequestBody UsernameOperation usernameOperation)
            throws BadRequestException {
        Status status = new Status(userService.updateAccess(usernameOperation));
        return ResponseEntity.status(HttpStatus.OK).body(status);
    }

    @DeleteMapping("/api/auth/user/{username}")
    public ResponseEntity<DeleteResponse> delete(@PathVariable String username) {
        userService.delete(username);
        DeleteResponse delete = new DeleteResponse(username, "Deleted successfully!");
        return ResponseEntity.status(HttpStatus.OK).body(delete);
    }

    //    <S, T> List<T> mapList(List<S> source, Class<T> targetClass) {
    //        return source
    //                .stream()
    //                .map(element -> modelMapper.map(element, targetClass))
    //                .collect(Collectors.toList());
    //    }
}















