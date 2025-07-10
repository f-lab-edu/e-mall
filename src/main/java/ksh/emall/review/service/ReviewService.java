package ksh.emall.review.service;

import ksh.emall.product.entity.ProductReviewStat;
import ksh.emall.product.repository.ProductRepository;
import ksh.emall.product.repository.ProductReviewStatRepository;
import ksh.emall.review.dto.request.ReviewRegisterRequestDto;
import ksh.emall.review.entity.Review;
import ksh.emall.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final ProductReviewStatRepository productReviewStatRepository;


    @Transactional
    public void register(long productId, ReviewRegisterRequestDto request) {
        productRepository.getById(productId);
        Review review = createReview(productId, request);
        reviewRepository.save(review);
        ProductReviewStat productReviewStat = productReviewStatRepository.getById(productId);
        productReviewStat.addReviewScore(request.getScore());
    }

    private static Review createReview(long productId, ReviewRegisterRequestDto request) {
        return Review.builder()
            .score(request.getScore())
            .title(request.getTitle())
            .body(request.getBody())
            .memberId(request.getMemberId())
            .productId(productId)
            .build();
    }
}
