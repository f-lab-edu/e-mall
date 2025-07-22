package ksh.emall.review.service;

import ksh.emall.common.exception.CustomException;
import ksh.emall.common.exception.ErrorCode;
import ksh.emall.product.entity.ProductReviewStat;
import ksh.emall.product.repository.ProductRepository;
import ksh.emall.product.repository.ProductReviewStatRepository;
import ksh.emall.review.dto.request.ReviewRegisterRequestDto;
import ksh.emall.review.dto.request.ReviewRequestDto;
import ksh.emall.review.entity.Review;
import ksh.emall.review.repository.ReviewRepository;
import ksh.emall.review.repository.projection.ReviewWithMember;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final ProductReviewStatRepository productReviewStatRepository;
    private final Clock clock;

    @Transactional(readOnly = true)
    public Page<ReviewWithMember> findReviewsOfProduct(
        Pageable pageable,
        Long productId,
        ReviewRequestDto request
    ) {
        return reviewRepository.findByProductIdAfterCursor(
            pageable,
            productId,
            request
        );
    }

    @Transactional
    public Review register(long productId, ReviewRegisterRequestDto request) {
        productRepository.getById(productId);

        boolean duplicated = reviewRepository.existsByProductIdAndMemberId(
            productId,
            request.getMemberId()
        );
        if (duplicated) {
            throw new CustomException(ErrorCode.REVIEW_ALREADY_REGISTERED);
        }


        Review review = createReview(productId, request);
        reviewRepository.save(review);

        ProductReviewStat productReviewStat = productReviewStatRepository.getByProductId(productId);
        productReviewStat.addReviewScore(request.getScore());

        return review;
    }

    private Review createReview(
        long productId,
        ReviewRegisterRequestDto request
    ) {
        return Review.builder()
            .score(request.getScore())
            .title(request.getTitle())
            .body(request.getBody())
            .registerDate(LocalDate.now(clock))
            .memberId(request.getMemberId())
            .productId(productId)
            .build();
    }
}
