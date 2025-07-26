package ksh.emall.review.service;

import ksh.emall.common.exception.CustomException;
import ksh.emall.common.exception.ErrorCode;
import ksh.emall.product.entity.DeliveryType;
import ksh.emall.product.entity.Product;
import ksh.emall.product.entity.ProductCategory;
import ksh.emall.product.entity.ProductReviewStat;
import ksh.emall.product.repository.ProductRepository;
import ksh.emall.product.repository.ProductReviewStatRepository;
import ksh.emall.review.dto.request.ReviewRegisterRequestDto;
import ksh.emall.review.entity.Review;
import ksh.emall.review.repository.ReviewRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@SpringBootTest
class ReviewServiceTest {

    @Autowired
    ReviewService reviewService;

    @Autowired
    ReviewRepository reviewRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    ProductReviewStatRepository productReviewStatRepository;

    @MockitoBean
    Clock clock;

    Product product;
    ProductReviewStat productReviewStat;

    @BeforeEach
    void setUp() {
        product = createProduct("빵", 1500);
        productRepository.save(product);

        productReviewStat = createProductReviewStat(42, 10, product.getId());
        productReviewStatRepository.save(productReviewStat);

        Instant fixed = LocalDate.of(2025,1,1)
            .atStartOfDay(ZoneId.systemDefault()).toInstant();
        given(clock.instant()).willReturn(fixed);
        given(clock.getZone()).willReturn(ZoneId.systemDefault());
    }

    @AfterEach
    void tearDown() {
        reviewRepository.deleteAllInBatch();
        productRepository.deleteAllInBatch();
        productReviewStatRepository.deleteAllInBatch();
    }

    @DisplayName("리뷰 등록 시 등록일이 오늘로 설정되고 상품 리뷰 통계에 점수가 반영된다")
    @Test
    void register1() {
        //given
        ReviewRegisterRequestDto request = new ReviewRegisterRequestDto(
            1L,
            "제목",
            "내용",
            3
        );

        //when
        Review registeredReview = reviewService.register(product.getId(), request);

        //then
        assertThat(registeredReview.getRegisterDate()).isEqualTo(LocalDate.of(2025,1,1));

        ProductReviewStat reviewStat = productReviewStatRepository.findByProductId(product.getId()).get();
        assertThat(reviewStat).extracting("totalScore", "reviewCount")
            .containsExactly(
                45,
                11
            );
    }

    @DisplayName("리뷰를 이미 등록한 상품에는 중복으로 리뷰를 등록할 수 없다")
    @Test
    void register2() {
        //given
        Review registeredReview = Review.builder()
            .productId(product.getId())
            .memberId(1L)
            .build();
        reviewRepository.save(registeredReview);

        ReviewRegisterRequestDto request = new ReviewRegisterRequestDto(
            1L,
            "제목",
            "내용",
            3
        );

        //when //then
        assertThatExceptionOfType(CustomException.class)
            .isThrownBy(() -> reviewService.register(product.getId(), request))
            .returns(ErrorCode.REVIEW_ALREADY_REGISTERED, CustomException::getErrorCode);
    }

    private Product createProduct(String name, int price) {
        return Product.builder()
            .category(ProductCategory.FOOD)
            .name(name)
            .brand("브랜드")
            .price(price)
            .deliveryType(DeliveryType.NORMAL)
            .quantity(13)
            .expectedDeliveryDays(3)
            .imageUrl("이미지 url")
            .isDeleted(false)
            .build();
    }

    private ProductReviewStat createProductReviewStat(
        int totalScore,
        int reviewCount,
        long productId
    ) {
        double avgScore = (double) totalScore / reviewCount;

        return ProductReviewStat.builder()
            .avgScore(avgScore)
            .totalScore(totalScore)
            .reviewCount(reviewCount)
            .productId(productId)
            .isDeleted(false)
            .build();
    }
}
