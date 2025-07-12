package ksh.emall.review.repository.production;

import ksh.emall.member.entity.Member;
import ksh.emall.review.entity.Review;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReviewWithMember {

    private Review review;
    private Member member;
}
