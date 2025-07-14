package ksh.emall.review.dto.response;

import ksh.emall.member.entity.Member;
import ksh.emall.review.entity.Review;
import ksh.emall.review.repository.projection.ReviewWithMember;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReviewResponseDto {

    private String author;
    private String title;
    private String body;
    private int likeCount;

    public static ReviewResponseDto from(ReviewWithMember reviewWithMember) {
        Review review = reviewWithMember.getReview();
        Member member = reviewWithMember.getMember();

        return ReviewResponseDto.builder()
            .author(member.getNickname())
            .title(review.getTitle())
            .body(review.getBody())
            .likeCount(review.getLikeCount())
            .build();
    }
}
