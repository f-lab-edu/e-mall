package ksh.emall.product.repository;

import ksh.emall.common.exception.CustomException;
import ksh.emall.common.exception.ErrorCode;
import ksh.emall.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long>, ProductQueryRepository {

    default Product getById(Long id) {
        return findById(id)
            .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND, List.of(id)));
    }
}
