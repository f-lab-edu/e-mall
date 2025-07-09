package ksh.emall.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    INTERNAL_SERVER_ERROR(500, "internal.server.error");

    private final int status;
    private final String messageKey;
}
