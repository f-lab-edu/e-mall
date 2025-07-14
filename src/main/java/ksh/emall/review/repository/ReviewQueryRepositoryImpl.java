package ksh.emall.review.repository;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.DatePath;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import ksh.emall.review.dto.request.ReviewRequestDto;
import ksh.emall.review.repository.production.ReviewWithMember;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.time.LocalDate;
import java.util.List;

import static ksh.emall.member.entity.QMember.member;
import static ksh.emall.review.entity.QReview.review;

@RequiredArgsConstructor
public class ReviewQueryRepositoryImpl implements ReviewQueryRepository {

    private JPAQueryFactory queryFactory;


    @Override
    public Page<ReviewWithMember> findByProductIdAfterCursor(
        Pageable pageable,
        long productId,
        ReviewRequestDto request
    ) {
        var cursorPredicate = cursorPredicate(pageable, request);

        List<ReviewWithMember> content = queryFactory
            .select(Projections.constructor(
                ReviewWithMember.class,
                review,
                member
            ))
            .from(review)
            .join(member).on(review.memberId.eq(member.id))
            .where(
                review.productId.eq(productId),
                cursorPredicate
            )
            .orderBy(
                cursorOrder(request),
                review.id.asc()
            )
            .fetch();


        return PageableExecutionUtils.getPage(
            content,
            pageable,
            () -> totalCount(cursorPredicate)
        );
    }

    private BooleanExpression cursorPredicate(
        Pageable pageable,
        ReviewRequestDto request
    ) {
        if (pageable.getPageNumber() == 0) return null;

        if (request.getLastScore() != null) {
            return scoreCursorPredicate(
                review.score,
                request.getLastScore(),
                request.getLastId(),
                request.getIsAscending()
            );
        }

        return registerDateCursorPredicate(
            review.registerDate,
            request.getLastRegisterDate(),
            request.getLastId(),
            request.getIsAscending()
        );
    }

    private <T extends Number & Comparable<? super T>> BooleanExpression scoreCursorPredicate(
        NumberPath<T> path,
        T lastValue,
        long lastId,
        boolean isAscending
    ) {
        BooleanExpression sameValuePredicate = path.eq(lastValue)
            .and(review.id.gt(lastId));

        BooleanExpression nextPagePredicate = isAscending
            ? path.gt(lastValue)
            : path.lt(lastValue);

        return sameValuePredicate.or(nextPagePredicate);
    }

    private BooleanExpression registerDateCursorPredicate(
        DatePath<LocalDate> path,
        LocalDate lastValue,
        long lastId,
        boolean isAscending
    ) {
        BooleanExpression sameValuePredicate = path.eq(lastValue)
            .and(review.id.gt(lastId));

        BooleanExpression nextPagePredicate = isAscending
            ? path.after(lastValue)
            : path.before(lastValue);

        return sameValuePredicate.or(nextPagePredicate);
    }

    private OrderSpecifier<?> cursorOrder(
        ReviewRequestDto request
    ) {
        Order direction = request.getIsAscending() ? Order.ASC : Order.DESC;

        return request.getLastScore() == null
            ? new OrderSpecifier<>(direction, review.createdAt)
            : new OrderSpecifier<>(direction, review.score);
    }

    private Long totalCount(Predicate where) {
        return queryFactory
            .select(review.count())
            .from(review)
            .where(where)
            .fetchOne();
    }
}
