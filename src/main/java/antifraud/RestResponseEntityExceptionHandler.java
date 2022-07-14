package antifraud;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.persistence.EntityNotFoundException;
import java.util.Map;
import java.util.NoSuchElementException;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(value = {NoSuchElementException.class, BadRequestException.class})
    public ResponseEntity handleNoSuchElementException(Exception ex, WebRequest request) {
        return ResponseEntity.badRequest().build();
        //probably add typical <ErrorMessage>
    }
    @ExceptionHandler(value = RegisterUserExistsException.class)
    public ResponseEntity handleRegisterUserExistsRequest(Exception ex, WebRequest request) {
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }
    @ExceptionHandler(value = UnauthorizedException.class)
    public ResponseEntity handleUnauthorizedRequest(Exception ex, WebRequest request) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
    @ExceptionHandler(value = {UsernameNotFoundException.class, EntityNotFoundException.class})
    public ResponseEntity handleUsernameNotFoundException(Exception ex, WebRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
    @ExceptionHandler(value = AssignExistingEntityException.class)
    public ResponseEntity handleAssignExistingRoleException(Exception ex, WebRequest request) {
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }
    @ExceptionHandler(value = UnprocessableException.class)
    public ResponseEntity handleUnprocessableException(Exception ex, WebRequest request) {
        Map<String, Integer> map = Map.of("id", 1);
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(map);
    }
}
