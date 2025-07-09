package ksh.emall.product.dto.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
public class ProductSearchConditionRequestDto {

    @NotBlank(message = "검색어는 필수입니다.")
    private String searchKeyword;

    private List<String> brands = new ArrayList<>();

    @Min(value = 0, message = "리뷰 점수대는 0에서 4 사이여야 합니다.")
    @Max(value = 4, message = "리뷰 점수대는 0에서 4 사이여야 합니다.")
    private Integer reviewScore;

    @Min(value = 1, message = "최소 희망 가격은 1원 이상이어야 합니다.")
    private Integer minPrice;

    @Min(value = 1, message = "최대 희망 가격은 1원 이상이어야 합니다.")
    private Integer maxPrice;

    @AssertTrue(message = "최소 희망 가격은 최대 희망 가격보다 클 수 없습니다.")
    private boolean isPriceRangeValid() {
        if (minPrice == null || maxPrice == null) {
            return true;
        }
        return minPrice < maxPrice;
    }
}
