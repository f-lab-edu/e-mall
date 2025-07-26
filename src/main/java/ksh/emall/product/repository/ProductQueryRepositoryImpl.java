package ksh.emall.product.repository;

import com.querydsl.core.types.*;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import ksh.emall.product.dto.request.ProductRequestDto;
import ksh.emall.product.dto.request.ProductSearchConditionRequestDto;
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
        Predicate where = product.category.eq(category);
        List content = getContent(pageable, where, criteria, isAscending);
        Long count = getCount(where);

        return new PageImpl<>(content, pageable, count);
    }

    @Override
    public Page<ProductWithReviewStat> findBySearchCondition(
        Pageable pageable,
        ProductRequestDto productRequest,
        ProductSearchConditionRequestDto searchCondition
    ) {
        Predicate where = ExpressionUtils.allOf(
            product.category.eq(productRequest.getCategory()),
            searchPredicate(searchCondition)
        );

        List<ProductWithReviewStat> content = getContent(
            pageable,
            where,
            productRequest.getCriteria(),
            productRequest.getIsAscending()
        );
        Long count = getCount(where);

        return new PageImpl<>(content, pageable, count);
    }

    private List getContent(
        Pageable pageable,
        Predicate where,
        ProductSortCriteria criteria,
        Boolean isAscending
    ) {
        return queryFactory
            .select(Projections.constructor(
                ProductWithReviewStat.class,
                product,
                productReviewStat
            ))
            .from(product)
            .join(productReviewStat).on(productReviewStat.productId.eq(product.id))
            .join(productSalesStat).on(productSalesStat.productId.eq(product.id))
            .where(where)
            .orderBy(
                orderSpecifier(criteria, isAscending),
                product.id.asc()
            )
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();
    }

    private Long getCount(Predicate where) {
        return queryFactory
            .select(product.count())
            .from(product)
            .join(productReviewStat).on(productReviewStat.productId.eq(product.id))
            .join(productSalesStat).on(productSalesStat.productId.eq(product.id))
            .where(where)
            .fetchOne();
    }

    private OrderSpecifier orderSpecifier(ProductSortCriteria criteria, boolean isAscending) {
        Order direction = isAscending ? Order.ASC : Order.DESC;

        if (criteria == ProductSortCriteria.PRICE) {
            return new OrderSpecifier(direction, product.price);
        }

        if (criteria == ProductSortCriteria.SOLD_COUNT) {
            return new OrderSpecifier(Order.DESC, productSalesStat.soldCount);
        }

        return new OrderSpecifier(direction, product.createdAt);
    }

    private Predicate searchPredicate(ProductSearchConditionRequestDto condition) {
        return ExpressionUtils.allOf(
            keywordPredicate(condition.getSearchKeyword()),
            brandsPredicate(condition.getBrands()),
            reviewScorePredicate(condition.getReviewScore()),
            priceRangePredicate(condition.getMinPrice(), condition.getMaxPrice())
        );
    }

    private BooleanExpression keywordPredicate(String keyword) {
        return product.name.containsIgnoreCase(keyword);
    }

    private BooleanExpression brandsPredicate(List<String> brands) {
        return brands != null && !brands.isEmpty()
            ? product.brand.in(brands)
            : null;
    }

    private BooleanExpression reviewScorePredicate(Integer score) {
        if (score == null) return null;

        var avgScore = productReviewStat.avgScore;

        var lower = avgScore.goe(score);
        var upper = score < 4 ? avgScore.lt(score + 1) : avgScore.loe(score + 1);

        return lower.and(upper);
    }

    private BooleanExpression priceRangePredicate(Integer minPrice, Integer maxPrice) {
        var price = product.price;
        BooleanExpression predicate = null;

        if (minPrice != null) {
            predicate = price.goe(minPrice);
        }

        if (maxPrice != null) {
            var upper = price.loe(maxPrice);
            predicate = (predicate == null) ? upper : predicate.and(upper);
        }

        return predicate;
    }
}
