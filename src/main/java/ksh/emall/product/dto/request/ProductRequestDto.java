package ksh.emall.product.dto.request;

import jakarta.validation.constraints.NotNull;
import ksh.emall.product.entity.ProductCategory;
import ksh.emall.product.enums.sort.ProductSortCriteria;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProductRequestDto {

    @NotNull(message = "제품 카테고리는 필수입니다.")
    private ProductCategory category;

    @NotNull(message = "제품 정렬 기준은 필수입니다.")
    private ProductSortCriteria criteria;

    @NotNull(message = "정렬 방향은 필수입니다.")
    @Getter(AccessLevel.NONE)
    private Boolean isAscending;

    public Boolean isAscending() {
        return this.isAscending;
    }
}
