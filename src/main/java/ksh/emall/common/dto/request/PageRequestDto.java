package ksh.emall.common.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Slf4j
@Getter
@AllArgsConstructor
public class PageRequestDto {

    @NotNull(message = "페이지 번호는 필수입니다.")
    @PositiveOrZero(message = "페이지 번호는 0 이상이어야 합니다.")
    private Integer page;

    @NotNull(message = "페이지 크기는 필수입니다.")
    @Min(value = 1, message = "페이지 크기는 최소 1 이상이어야 합니다.")
    @Max(value = 50, message = "페이지 크기는 최대 50입니다.")
    private Integer size;

    public Pageable toPageable() {
        return PageRequest.of(page, size);
    }
}
