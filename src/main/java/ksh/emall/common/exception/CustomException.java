package ksh.emall.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class CustomException extends RuntimeException {

    private final ErrorCode errorCode;
    private final List<Object> messageArgs;

    public CustomException(ErrorCode errorCode) {
        this.errorCode = errorCode;
        this.messageArgs = List.of();
    }
}
