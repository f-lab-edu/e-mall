package ksh.emall.product.controller;

import jakarta.validation.Valid;
import ksh.emall.common.dto.request.PageRequestDto;
import ksh.emall.common.dto.response.PageResponseDto;
import ksh.emall.product.dto.request.ProductRequestDto;
import ksh.emall.product.dto.request.ProductSearchConditionRequestDto;
import ksh.emall.product.dto.response.ProductResponseDto;
import ksh.emall.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/products")
    public ResponseEntity<PageResponseDto> findProductsByCategory(
        @Valid PageRequestDto pageRequest,
        @Valid ProductRequestDto productRequest
    ) {
        Page<ProductResponseDto> page = productService
            .findProductsByCategory(pageRequest.toPageable(), productRequest)
            .map(ProductResponseDto::from);

        PageResponseDto<ProductResponseDto> response = PageResponseDto.from(page);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/products/search")
    public ResponseEntity<PageResponseDto> searchProductsWithConditions(
        @Valid PageRequestDto pageRequest,
        @Valid ProductRequestDto productRequest,
        @Valid ProductSearchConditionRequestDto productSearchRequestDto
    ) {
        Page<ProductResponseDto> page = productService.searchProducts(
                pageRequest.toPageable(),
                productRequest,
                productSearchRequestDto
            )
            .map(ProductResponseDto::from);

        PageResponseDto<ProductResponseDto> response = PageResponseDto.from(page);

        return ResponseEntity.ok(response);
    }
}
