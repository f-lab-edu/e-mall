package ksh.emall.review.dto.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import ksh.emall.review.enums.sort.ReviewSortCriteria;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.hibernate.validator.constraints.Range;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class ReviewRequestDto {

    @NotNull(message = "정렬 기준은 필수입니다.")
    private ReviewSortCriteria criteria;

    @NotNull(message = "정렬 방향은 필수입니다.")
    private Boolean isAscending;

    @NotNull(message = "이전 페이지의 마지막 리뷰 id는 필수입니다.")
    private Long lastId;

    @Range(min = 0, max = 5, message = "이전 페이지의 마지막 리뷰 점수는 0이상 5이하입니다.")
    private Integer lastScore;

    private LocalDate lastRegisterDate;

    @AssertTrue(message = "이전 페이지 마지막 리뷰의 점수와 등록일 중 하나만 입력해야 합니다.")
    private boolean hasOnlyOne() {
        boolean hasScore = this.lastScore != null;
        boolean hasRegisterDate = this.lastRegisterDate != null;

        return hasScore ^ hasRegisterDate;
    }
}
