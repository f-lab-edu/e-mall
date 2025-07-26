package ksh.emall.review.repository;

import ksh.emall.member.entity.Member;
import ksh.emall.member.repository.MemberRepository;
import ksh.emall.review.dto.request.ReviewRequestDto;
import ksh.emall.review.entity.Review;
import ksh.emall.review.enums.sort.ReviewSortCriteria;
import ksh.emall.review.repository.projection.ReviewWithMember;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@SpringBootTest
class ReviewRepositoryTest {

    @Autowired
    ReviewRepository reviewRepository;

    @Autowired
    MemberRepository memberRepository;

    private List<Review> reviews;

    @BeforeEach
    void setUp() {
        Member member1 = createMember("멤버1");
        Member member2 = createMember("멤버2");
        Member member3 = createMember("멤버3");
        Member member4 = createMember("멤버4");
        Member member5 = createMember("멤버5");
        Member member6 = createMember("멤버6");
        memberRepository.saveAll(List.of(member1, member2, member3, member4, member5, member6));

        Review review1 = createReview(
            5, "최고예요", LocalDate.of(2025, 1, 1),
            member1.getId(), 100L
        );
        Review review2 = createReview(
            4, "괜찮아요", LocalDate.of(2025, 1, 1),
            member3.getId(), 100L
        );
        Review review3 = createReview(
            4, "추천합니다", LocalDate.of(2025, 1, 3),
            member4.getId(), 100L
        );
        Review review4 = createReview(
            5, "정말 좋아요", LocalDate.of(2025, 1, 2),
            member2.getId(), 100L
        );
        Review review5 = createReview(
            3, "그럭저럭", LocalDate.of(2025, 1, 2),
            member6.getId(), 100L
        );
        Review review6 = createReview(
            3, "보통이에요", LocalDate.of(2025, 1, 2),
            member5.getId(), 100L
        );
        reviews = List.of(review1, review2, review3, review4, review5, review6);
        reviewRepository.saveAll(reviews);
    }

    @AfterEach
    void tearDown() {
        reviewRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
    }

    @DisplayName("특정 제품의 리뷰를 점수 순으로 페이징 조회할 때 첫 페이지는 이전 페이지 마지막 리뷰의 점수를 입력할 필요가 없다")
    @Test
    void findByProductIdAfterCursor1() {
        //given
        Pageable pageable = PageRequest.of(0, 3);
        long productId = 100L;
        ReviewRequestDto requestDto = new ReviewRequestDto(
            ReviewSortCriteria.SCORE,
            false,
            null,
            null,
            null
        );

        //when
        Page<ReviewWithMember> page = reviewRepository.findByProductIdAfterCursor(
            pageable,
            productId,
            requestDto
        );

        //then
        assertThat(page.getContent()).hasSize(3)
            .extracting("review.title", "member.name")
            .containsExactly(
                tuple("최고예요", "멤버1"),
                tuple("정말 좋아요", "멤버2"),
                tuple("괜찮아요", "멤버3")
            );

        assertThat(page.hasNext()).isTrue();
    }

    @DisplayName("이전 페이지 마지막 레코드의 점수가 주어지면 특정 제품의 리뷰를 점수 순으로 페이징하고 동점자는 id 오름차순으로 정렬한다.")
    @Test
    void findByProductIdAfterCursor2() {
        //given
        Pageable pageable = PageRequest.of(1, 3);
        long productId = 100L;
        Review lastReview = reviews.get(1);
        ReviewRequestDto requestDto = new ReviewRequestDto(
            ReviewSortCriteria.SCORE,
            false,
            lastReview.getId(),
            lastReview.getScore(),
            null
        );

        //when
        Page<ReviewWithMember> page = reviewRepository.findByProductIdAfterCursor(
            pageable,
            productId,
            requestDto
        );

        //then
        assertThat(page.getContent()).hasSize(3)
            .extracting("review.title", "member.name")
            .containsExactly(
                tuple("추천합니다", "멤버4"),
                tuple("그럭저럭", "멤버6"),
                tuple("보통이에요", "멤버5")
            );

        assertThat(page.hasNext()).isFalse();
    }

    @DisplayName("특정 제품의 리뷰를 등록일 순으로 페이징 조회할 때 첫 페이지는 이전 페이지 마지막 리뷰의 점수를 입력할 필요가 없다")
    @Test
    void findByProductIdAfterCursor3() {
        //given
        Pageable pageable = PageRequest.of(0, 3);
        long productId = 100L;
        ReviewRequestDto requestDto = new ReviewRequestDto(
            ReviewSortCriteria.REGISTER_DATE,
            false,
            null,
            null,
            null
        );

        //when
        Page<ReviewWithMember> page = reviewRepository.findByProductIdAfterCursor(
            pageable,
            productId,
            requestDto
        );

        //then
        assertThat(page.getContent()).hasSize(3)
            .extracting("review.title", "member.name")
            .containsExactly(
                tuple("추천합니다", "멤버4"),
                tuple("정말 좋아요", "멤버2"),
                tuple("그럭저럭", "멤버6")
            );

        assertThat(page.hasNext()).isTrue();
    }

    @DisplayName("이전 페이지 마지막 레코드의 점수가 주어지면 특정 제품의 리뷰를 등록일 순으로 페이징하고 등록일이 같은 레코드는 id 오름차순으로 정렬한다.")
    @Test
    void findByProductIdAfterCursor4() {
        //given
        Pageable pageable = PageRequest.of(1, 3);
        long productId = 100L;
        Review lastReview = reviews.get(4);
        ReviewRequestDto requestDto = new ReviewRequestDto(
            ReviewSortCriteria.REGISTER_DATE,
            false,
            lastReview.getId(),
            null,
            lastReview.getRegisterDate()
        );

        //when
        Page<ReviewWithMember> page = reviewRepository.findByProductIdAfterCursor(
            pageable,
            productId,
            requestDto
        );

        //then
        assertThat(page.getContent()).hasSize(3)
            .extracting("review.title", "member.name")
            .containsExactly(
                tuple("보통이에요", "멤버5"),
                tuple("최고예요", "멤버1"),
                tuple("괜찮아요", "멤버3")
            );

        assertThat(page.hasNext()).isFalse();
    }

    private Review createReview(
        int score,
        String title,
        LocalDate registerDate,
        long memberId,
        long productId
    ) {
        return Review.builder()
            .score(score)
            .title(title)
            .body("리뷰 본문")
            .likeCount(5)
            .registerDate(registerDate)
            .memberId(memberId)
            .productId(productId)
            .isDeleted(false)
            .build();
    }

    private Member createMember(
        String name
    ) {
        return Member.builder()
            .email("email@email.com")
            .password("password")
            .nickname("nickname")
            .name(name)
            .build();
    }
}
