package ksh.emall.product.repository;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import ksh.emall.product.entity.ProductCategory;
import ksh.emall.product.enums.sort.ProductSortCriteria;
import ksh.emall.product.repository.projection.ProductWithReviewStat;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static ksh.emall.product.entity.QProduct.product;
import static ksh.emall.product.entity.QProductReviewStat.productReviewStat;
import static ksh.emall.product.entity.QProductSalesStat.productSalesStat;

@RequiredArgsConstructor
public class ProductQueryRepositoryImpl implements ProductQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<ProductWithReviewStat> findByCategoryWithReviewStat(
        Pageable pageable,
        ProductCategory category,
        ProductSortCriteria criteria,
        boolean isAscending
    ) {
        List<ProductWithReviewStat> content = queryFactory
            .select(Projections.constructor(
                ProductWithReviewStat.class,
                product,
                productReviewStat
            ))
            .from(product)
            .join(productReviewStat)
                .on(productReviewStat.productId.eq(product.id))
            .join(productSalesStat)
                .on(productSalesStat.productId.eq(product.id))
            .where(product.category.eq(category))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .orderBy(buildProductOrderSpecifier(criteria, isAscending))
            .fetch();

        Long count = queryFactory.select(product.count())
            .from(product)
            .fetchOne();

        return new PageImpl<>(content, pageable, count);
    }

    @Override
    public List<String> findBrandDistinctByCategory(ProductCategory category) {
        return queryFactory
            .select(product.brand).distinct()
            .from(product)
            .where(product.category.eq(category))
            .fetch();
    }

    OrderSpecifier buildProductOrderSpecifier(ProductSortCriteria criteria, boolean isAscending) {
        Order direction = isAscending ? Order.ASC : Order.DESC;

        if(criteria == ProductSortCriteria.PRICE) {
            return new OrderSpecifier(direction, product.price);
        }

        if(criteria == ProductSortCriteria.SOLD_COUNT) {
            return new OrderSpecifier(Order.DESC, productSalesStat.soldCount);
        }

        return new OrderSpecifier(direction, product.createdAt);
    }
}
