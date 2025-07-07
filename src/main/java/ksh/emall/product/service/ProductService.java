package ksh.emall.product.service;

import ksh.emall.product.dto.request.ProductRequestDto;
import ksh.emall.product.dto.request.ProductSearchConditionRequestDto;
import ksh.emall.product.entity.ProductCategory;
import ksh.emall.product.repository.ProductRepository;
import ksh.emall.product.repository.projection.ProductWithReviewStat;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
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

    @Transactional(readOnly = true)
    public List<String> findAllBrandsOfCategory(ProductCategory category) {
        return productRepository.findBrandDistinctByCategory(category);
    }

    @Transactional(readOnly = true)
    public Page<ProductWithReviewStat> searchProducts(
        Pageable pageable,
        ProductRequestDto productRequest,
        ProductSearchConditionRequestDto productSearchRequestDto
    ) {
        return productRepository.findBySearchCondition(
            pageable,
            productRequest,
            productSearchRequestDto
        );
    }
}
