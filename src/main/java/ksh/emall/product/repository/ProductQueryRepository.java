package ksh.emall.product.repository;

import ksh.emall.product.entity.ProductCategory;
import ksh.emall.product.enums.sort.ProductSortCriteria;
import ksh.emall.product.repository.projection.ProductWithReviewStat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductQueryRepository {

    Page<ProductWithReviewStat> findByCategoryWithReviewStat(Pageable pageable, ProductCategory category, ProductSortCriteria criteria, boolean isAscending);
}
