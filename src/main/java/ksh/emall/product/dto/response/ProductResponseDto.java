package ksh.emall.product.dto.response;

import ksh.emall.product.entity.DeliveryType;
import ksh.emall.product.entity.Product;
import ksh.emall.product.entity.ProductReviewStat;
import ksh.emall.product.repository.projection.ProductWithReviewStat;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class ProductResponseDto {

    private String name;
    private int price;
    private DeliveryType deliveryType;
    private LocalDate guaranteedDeliveryDate;
    private double averageScore;
    private int reviewCount;
    private String imageUrl;

    public static ProductResponseDto from(ProductWithReviewStat productWithReviewStat) {
        Product product = productWithReviewStat.getProduct();
        ProductReviewStat reviewStat = productWithReviewStat.getReviewStat();

        return ProductResponseDto.builder()
            .name(product.getName())
            .price(product.getPrice())
            .deliveryType(product.getDeliveryType())
            .guaranteedDeliveryDate(LocalDate.now().plusDays(product.getExpectedDeliveryDays()))
            .averageScore(reviewStat.getAvgScore())
            .reviewCount(reviewStat.getReviewCount())
            .imageUrl(product.getImageUrl())
            .build();
    }
}
