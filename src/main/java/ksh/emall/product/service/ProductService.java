package ksh.emall.product.service;

import ksh.emall.product.dto.request.ProductRequestDto;
import ksh.emall.product.repository.ProductRepository;
import ksh.emall.product.repository.projection.ProductWithReviewStat;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public Page<ProductWithReviewStat> findProductsByCategory(
        Pageable pageable,
        ProductRequestDto productRequest
    ) {
        return productRepository.findByCategoryWithReviewStat(
            pageable,
            productRequest.getCategory(),
            productRequest.getCriteria(),
            productRequest.getIsAscending()
        );
    }
}
