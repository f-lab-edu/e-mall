package ksh.emall.common.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponseDto {

    private int status;
    private String code;
    private String message;

    public static ErrorResponseDto of(int status, String code, String message) {
        return new ErrorResponseDto(status, code, message);
    }
}
