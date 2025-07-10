package ksh.emall.review.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.hibernate.validator.constraints.Range;

@Getter
@AllArgsConstructor
public class ReviewRegisterRequestDto {

    @NotNull(message = "리뷰 등록자의 id는 필수입니다.")
    private Long memberId;

    @NotBlank(message = "리뷰 제목은 필수입니다.")
    private String title;

    @NotBlank(message = "리뷰 내용은 필수입니다.")
    @Size(min = 20, max = 1500, message = "리뷰는 20자 이상 1500자 이하로 작성해야 합니다.")
    private String body;

    @NotNull(message = "리뷰 별점은 필수입니다.")
    @Range(min = 0, max = 5, message = "별점은 0이상 5이하입니다.")
    private Integer score;
}
