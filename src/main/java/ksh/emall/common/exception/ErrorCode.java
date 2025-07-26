package ksh.emall.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    PRODUCT_NOT_FOUND(404, "product.not.found"),
    REVIEW_ALREADY_REGISTERED(400, "review.already.registered"),

    INTERNAL_SERVER_ERROR(500, "internal.server.error");

    private final int status;
    private final String messageKey;
}
