package ksh.emall.review.controller;

import jakarta.validation.Valid;
import ksh.emall.review.dto.request.ReviewRegisterRequestDto;
import ksh.emall.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

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
