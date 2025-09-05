package co.com.pragma.usecase.user.user.validations.error;

public class UserValidationException extends RuntimeException {
    public UserValidationException(String message) {
        super(message);
    }
}
