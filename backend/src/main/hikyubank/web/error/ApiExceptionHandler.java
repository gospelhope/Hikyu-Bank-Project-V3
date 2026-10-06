package hikyubank.web.error;

import hikyubank.application.exception.BankException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(BankException.class)
    public ResponseEntity<ApiError> applicationError(BankException error) {
        return ResponseEntity.status(error.status())
            .body(new ApiError(error.code(), error.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> validationError(MethodArgumentNotValidException error) {
        var first = error.getBindingResult().getFieldErrors().get(0);
        String message = first.getField() + ": " + first.getDefaultMessage();
        return ResponseEntity.badRequest().body(new ApiError("VALIDATION_ERROR", message));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> invalidJson(HttpMessageNotReadableException error) {
        return ResponseEntity.badRequest().body(new ApiError(
            "INVALID_JSON", "Provide a valid JSON request body."
        ));
    }

    public record ApiError(String code, String message) {}
}
