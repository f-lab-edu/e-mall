package ksh.emall.product.controller;

import jakarta.validation.Valid;
import ksh.emall.common.dto.response.PageResponseDto;
import ksh.emall.product.dto.request.ProductRequestDto;
import ksh.emall.product.dto.response.ProductResponseDto;
import ksh.emall.product.dto.response.ProductSearchResponseDto;
import ksh.emall.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/products")
    public ResponseEntity<ProductSearchResponseDto> findProductsByCategory(
        Pageable pageable,
        @Valid ProductRequestDto productRequest
    ) {
        Page<ProductResponseDto> page = productService.findProductsByCategory(
                pageable,
                productRequest
            )
            .map(ProductResponseDto::from);

        PageResponseDto<ProductResponseDto> pageResponse = PageResponseDto.from(page);

        List<String> brands = productService.findAllBrandsOfCategory(productRequest.getCategory());

        ProductSearchResponseDto response = ProductSearchResponseDto.of(pageResponse, brands);

        return ResponseEntity.ok(response);
    }
}
