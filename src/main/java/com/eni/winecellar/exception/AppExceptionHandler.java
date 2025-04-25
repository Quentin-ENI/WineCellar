package com.eni.winecellar.exception;

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
    public ResponseEntity<String> handleException(Exception exception) {
        return new ResponseEntity<String>(exception.getMessage(), HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler(value={MethodArgumentNotValidException.class})
    public ResponseEntity<String> handleMethodArgumentNotValidException(
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

        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(message);
    }
}
