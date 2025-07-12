package ksh.emall.review.repository;

import ksh.emall.review.dto.request.ReviewRequestDto;
import ksh.emall.review.repository.production.ReviewWithMember;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReviewQueryRepository {

    Page<ReviewWithMember> findByProductIdAfterCursor(Pageable pageable, long productId, ReviewRequestDto request);
}
