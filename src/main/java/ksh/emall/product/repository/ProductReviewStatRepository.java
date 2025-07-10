package ksh.emall.product.repository;

import ksh.emall.common.exception.CustomException;
import ksh.emall.common.exception.ErrorCode;
import ksh.emall.product.entity.ProductReviewStat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductReviewStatRepository extends JpaRepository<ProductReviewStat, Long> {

    default ProductReviewStat getByProductId(Long productId) {
        return findByProductId(productId)
            .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND, List.of(productId)));
    }

    Optional<ProductReviewStat> findByProductId(Long productId);
}
