package ksh.emall.common.exception;

import ksh.emall.common.dto.response.ErrorResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Locale;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalControllerAdvice {

    private final MessageSource messageSource;

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorResponseDto> handleBindException(BindException e) {
        String message = e.getBindingResult()
            .getAllErrors()
            .get(0)
            .getDefaultMessage();

        log.info("예외 정보 : {}", message);

        ErrorResponseDto response = ErrorResponseDto.of(
            HttpStatus.BAD_REQUEST.value(),
            HttpStatus.BAD_REQUEST.name(),
            message
        );

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(response);
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponseDto> handleCustomException(CustomException e, Locale locale) {
        ErrorCode errorCode = e.getErrorCode();
        String message = messageSource.getMessage(
            errorCode.getMessageKey(),
            e.getMessageArgs().toArray(),
            locale
        );

        log.info("예외 정보 : {} {}", errorCode.name(), message);

        ErrorResponseDto response = ErrorResponseDto.of(
            errorCode.getStatus(),
            errorCode.name(),
            message
        );

        return ResponseEntity
            .status(errorCode.getStatus())
            .body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleException(Exception e, Locale locale) {
        log.info("예외 정보 : {}", e.getMessage());

        ErrorResponseDto response = ErrorResponseDto.of(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            HttpStatus.INTERNAL_SERVER_ERROR.name(),
            e.getMessage()
        );

        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(response);
    }
}
