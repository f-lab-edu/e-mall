package ksh.emall.product.repository;

import ksh.emall.product.dto.request.ProductRequestDto;
import ksh.emall.product.dto.request.ProductSearchConditionRequestDto;
import ksh.emall.product.entity.ProductCategory;
import ksh.emall.product.enums.sort.ProductSortCriteria;
import ksh.emall.product.repository.projection.ProductWithReviewStat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductQueryRepository {

    Page<ProductWithReviewStat> findByCategoryWithReviewStat(Pageable pageable, ProductCategory category, ProductSortCriteria criteria, boolean isAscending);

    Page<ProductWithReviewStat> findBySearchCondition(Pageable pageable, ProductRequestDto productRequest, ProductSearchConditionRequestDto searchCondition);
}
