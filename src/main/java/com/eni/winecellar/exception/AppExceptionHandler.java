package com.eni.winecellar.exception;

import com.eni.winecellar.controller.ApiResponse;
import lombok.AllArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Locale;

@ControllerAdvice
@AllArgsConstructor
public class AppExceptionHandler {
    private final MessageSource messageSource;

    @ExceptionHandler(value={Exception.class})
    public ResponseEntity<ApiResponse<String>> handleException(Exception exception) {
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(
                new ApiResponse<>(
                        ApiResponse.NOT_SUCCESSFUL,
                        exception.getMessage(),
                        null
                )
        );
    }

    @ExceptionHandler(value={MethodArgumentNotValidException.class})
    public ResponseEntity<ApiResponse<String>> handleMethodArgumentNotValidException(
        MethodArgumentNotValidException exception,
        Locale locale
    ) {
        final String notValidExceptionMessage = messageSource.getMessage("not-valid-exception", null, locale);

        String message = exception.getFieldErrors()
            .stream()
            .map(
                    exceptionElement -> exceptionElement.getDefaultMessage()
            )
            .reduce(
                    notValidExceptionMessage,
                    (nvem, mess) -> {
                        assert mess != null;
                        return nvem.concat(mess).concat("\n");
                    }
            );

        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(
                new ApiResponse<>(
                        ApiResponse.NOT_SUCCESSFUL,
                        message,
                        null
                )
        );
    }
}
