package ksh.emall.product.repository.projection;

import ksh.emall.product.entity.Product;
import ksh.emall.product.entity.ProductReviewStat;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProductWithReviewStat {

    private Product product;
    private ProductReviewStat reviewStat;
}
