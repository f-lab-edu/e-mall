package ksh.emall.product.repository;

import jakarta.persistence.LockModeType;
import ksh.emall.common.exception.CustomException;
import ksh.emall.common.exception.ErrorCode;
import ksh.emall.product.entity.ProductReviewStat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.List;
import java.util.Optional;

public interface ProductReviewStatRepository extends JpaRepository<ProductReviewStat, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    default ProductReviewStat getByProductId(Long productId) {
        return findByProductId(productId)
            .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND, List.of(productId)));
    }

    Optional<ProductReviewStat> findByProductId(Long productId);
}
