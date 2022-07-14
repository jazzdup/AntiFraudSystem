package antifraud;

public class BadRequestException extends Throwable {
    public BadRequestException(String message) {
        super(message);
    }
}

class AssignExistingEntityException extends Throwable{
}

class RegisterUserExistsException extends Throwable {
}

class UnauthorizedException extends Throwable {
}

class UnprocessableException extends Throwable {
}