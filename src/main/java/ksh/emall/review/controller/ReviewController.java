package ksh.emall.review.controller;

import jakarta.validation.Valid;
import ksh.emall.common.dto.request.PageRequestDto;
import ksh.emall.common.dto.response.PageResponseDto;
import ksh.emall.review.dto.request.ReviewRegisterRequestDto;
import ksh.emall.review.dto.request.ReviewRequestDto;
import ksh.emall.review.dto.response.ReviewResponseDto;
import ksh.emall.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/products/{productId}")
    public ResponseEntity<PageResponseDto> findReviewsOfProduct(
        @Valid PageRequestDto pageRequest,
        @PathVariable("productId") long productId,
        ReviewRequestDto request
    ) {
        Page<ReviewResponseDto> page = reviewService.findReviewsOfProduct(
            pageRequest.toPageable(), productId, request)
            .map(ReviewResponseDto::from);

        var response = PageResponseDto.from(page);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/products/{productId}/reviews")
    public ResponseEntity<Void> register(
        @PathVariable("productId") long productId,
        @Valid @RequestBody ReviewRegisterRequestDto request
    ) {
        reviewService.register(productId, request);

        return ResponseEntity
            .noContent()
            .build();
    }
}
