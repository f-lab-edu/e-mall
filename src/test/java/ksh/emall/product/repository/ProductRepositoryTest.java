package ksh.emall.product.repository;

import ksh.emall.product.dto.request.ProductRequestDto;
import ksh.emall.product.dto.request.ProductSearchConditionRequestDto;
import ksh.emall.product.entity.*;
import ksh.emall.product.enums.sort.ProductSortCriteria;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@SpringBootTest
class ProductRepositoryTest {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    ProductReviewStatRepository productReviewStatRepository;

    @Autowired
    ProductSalesStatRepository productSalesStatRepository;

    @BeforeEach
    void setUp() {
        List<TestProduct> foods = List.of(
            new TestProduct("핫치킨 피자", 1000, "피자집", 5.0, 1),
            new TestProduct("후라이드 치킨", 3000, "치킨집", 4.0, 4),
            new TestProduct("양념 치킨", 500,  "치킨집", 3.9, 2),
            new TestProduct("짜장면",     4000,"중국집", 1.2, 2),
            new TestProduct("짬뽕",      2000,"중국집", 5.0, 5)
        );
        for (TestProduct food : foods) {
            saveWithStats(food, ProductCategory.FOOD);
        }

        TestProduct fashion = new TestProduct("옷", 1500, "옷집", 2.9, 1);
        saveWithStats(fashion, ProductCategory.FASHION);
    }

    @AfterEach
    void tearDown() {
        productRepository.deleteAllInBatch();
        productReviewStatRepository.deleteAllInBatch();
        productSalesStatRepository.deleteAllInBatch();
    }

    private static class TestProduct {
        private String name;
        private int price;
        private String brand;
        private double avgScore;
        private long soldCount;

        TestProduct(String name, int price, String brand, double avgScore, long soldCount) {
            this.name = name;
            this.price = price;
            this.brand = brand;
            this.avgScore = avgScore;
            this.soldCount = soldCount;
        }
    }

    private void saveWithStats(TestProduct data, ProductCategory category) {
        Product product = createProduct(category, data.name, data.price, data.brand);
        productRepository.save(product);

        ProductReviewStat reviewStat = createProductReviewStat(product.getId(), data.avgScore);
        productReviewStatRepository.save(reviewStat);

        ProductSalesStat salesStat = createProductSalesStat(product.getId(), data.soldCount);
        productSalesStatRepository.save(salesStat);
    }

    @DisplayName("가격을 기준을 정렬된 특정 카테고리의 제품 목록을 리뷰 정보와 함께 페이지하여 조회한다")
    @Test
    void findByCategoryWithReviewStat1() {
        //given
        PageRequest pageRequest = PageRequest.of(0, 3);
        ProductCategory category = ProductCategory.FOOD;
        ProductSortCriteria criteria = ProductSortCriteria.PRICE;
        boolean isAscending = false;

        //when
        var page = productRepository.findByCategoryWithReviewStat(
            pageRequest,
            category,
            criteria,
            isAscending
        );

        //then
        assertThat(page.getContent()).hasSize(3)
            .extracting("product.name", "reviewStat.averageReviewScore")
            .containsExactly(
                tuple("짜장면", 1.2),
                tuple("후라이드 치킨", 4.0),
                tuple("짬뽕", 5.0)
            );

        assertThat(page.hasNext()).isTrue();
    }

    @DisplayName("판매량을 기준을 정렬된 특정 카테고리의 제품 목록을 리뷰 정보와 함께 페이지하여 조회한다")
    @Test
    void findByCategoryWithReviewStat2() {
        //given
        PageRequest pageRequest = PageRequest.of(0, 4);
        ProductCategory category = ProductCategory.FOOD;
        ProductSortCriteria criteria = ProductSortCriteria.SOLD_COUNT;
        boolean isAscending = true;

        //when
        var page = productRepository.findByCategoryWithReviewStat(
            pageRequest,
            category,
            criteria,
            isAscending
        );

        //then
        assertThat(page.getContent()).hasSize(4)
            .extracting("product.name", "reviewStat.averageReviewScore")
            .containsExactly(
                tuple("짬뽕", 5.0),
                tuple("후라이드 치킨", 4.0),
                tuple("양념 치킨", 3.9),
                tuple("짜장면", 1.2)
            );

        assertThat(page.hasNext()).isTrue();
    }

    @DisplayName("등록 시각을 기준을 정렬된 특정 카테고리의 제품 목록을 리뷰 정보와 함께 페이지하여 조회한다")
    @Test
    void findByCategoryWithReviewStat3() {
        //given
        PageRequest pageRequest = PageRequest.of(0, 3);
        ProductCategory category = ProductCategory.FOOD;
        ProductSortCriteria criteria = ProductSortCriteria.REGISTER_DATE;
        boolean isAscending = false;

        //when
        var page = productRepository.findByCategoryWithReviewStat(
            pageRequest,
            category,
            criteria,
            isAscending
        );

        //then
        assertThat(page.getContent()).hasSize(3)
            .extracting("product.name", "reviewStat.averageReviewScore")
            .containsExactly(
                tuple("짬뽕", 5.0),
                tuple("짜장면", 1.2),
                tuple("양념 치킨", 3.9)
            );

        assertThat(page.hasNext()).isTrue();
    }

    @DisplayName("특정 카테고리의 제품을 브랜드 필터와 함께 제품명으로 검색할 수 있다.")
    @Test
    void findBySearchCondition1() {
        //given
        PageRequest pageRequest = PageRequest.of(0, 3);

        var productRequest = new ProductRequestDto(
            ProductCategory.FOOD,
            ProductSortCriteria.REGISTER_DATE,
            false
        );

        var searchRequest = new ProductSearchConditionRequestDto(
            "치킨",
            List.of("치킨집", "피자집"),
            null,
            null,
            null
        );

        //when
        var page = productRepository.findBySearchCondition(
            pageRequest,
            productRequest,
            searchRequest
        );

        //then
        assertThat(page.getContent()).hasSize(3)
            .extracting("product.name", "reviewStat.averageReviewScore")
            .containsExactly(
                tuple("양념 치킨", 3.9),
                tuple("후라이드 치킨", 4.0),
                tuple("핫치킨 피자", 5.0)
            );

        assertThat(page.hasNext()).isFalse();
    }

    @DisplayName("특정 카테고리의 제품을 리뷰 평균 점수대 필터와 함께 제품명으로 검색할 수 있다.")
    @Test
    void findBySearchCondition2() {
        //given
        PageRequest pageRequest = PageRequest.of(0, 3);

        var productRequest = new ProductRequestDto(
            ProductCategory.FOOD,
            ProductSortCriteria.REGISTER_DATE,
            false
        );

        var searchRequest = new ProductSearchConditionRequestDto(
            "치킨",
            null,
            4,
            null,
            null
        );

        //when
        var page = productRepository.findBySearchCondition(
            pageRequest,
            productRequest,
            searchRequest
        );

        //then
        assertThat(page.getContent()).hasSize(2)
            .extracting("product.name", "reviewStat.averageReviewScore")
            .containsExactly(
                tuple("후라이드 치킨", 4.0),
                tuple("핫치킨 피자", 5.0)
            );

        assertThat(page.hasNext()).isFalse();
    }

    @DisplayName("특정 카테고리의 제품을 가격 범위 필터와 함께 제품명으로 검색할 수 있다.")
    @Test
    void findBySearchCondition3() {
        //given
        PageRequest pageRequest = PageRequest.of(0, 3);

        var productRequest = new ProductRequestDto(
            ProductCategory.FOOD,
            ProductSortCriteria.PRICE,
            true
        );

        var searchRequest = new ProductSearchConditionRequestDto(
            "치킨",
            null,
            null,
            500,
            2999
        );

        //when
        var page = productRepository.findBySearchCondition(
            pageRequest,
            productRequest,
            searchRequest
        );

        //then
        assertThat(page.getContent()).hasSize(2)
            .extracting("product.name", "reviewStat.averageReviewScore")
            .containsExactly(
                tuple("양념 치킨", 3.9),
                tuple("핫치킨 피자", 5.0)
            );

        assertThat(page.hasNext()).isFalse();
    }

    private Product createProduct(
        ProductCategory category,
        String name,
        int price,
        String brand
    ) {
        return Product.builder()
            .category(category)
            .name(name)
            .brand(brand)
            .price(price)
            .deliveryType(DeliveryType.FAST)
            .quantity(10)
            .expectedDeliveryDays(3)
            .imageUrl("이미지 url")
            .isDeleted(false)
            .build();
    }

    private ProductReviewStat createProductReviewStat(
        long productId,
        double avgScore
    ) {
        return ProductReviewStat.builder()
            .averageReviewScore(avgScore)
            .reviewCount(10)
            .productId(productId)
            .build();
    }

    private ProductSalesStat createProductSalesStat(
        long productId,
        long soldCount
    ) {
        return ProductSalesStat.builder()
            .productId(productId)
            .soldCount(soldCount)
            .build();
    }
}
