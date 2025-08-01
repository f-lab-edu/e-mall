package ksh.emall.review.entity;

import jakarta.persistence.*;
import ksh.emall.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDate;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@SQLDelete(sql = "update review set is_deleted = true where id = ?")
@Where(clause = "is_deleted = false")
public class Review extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer score;

    private String title;

    @Column(length = 1500)
    private String body;

    private Integer likeCount;

    private LocalDate registerDate;

    private String imageUrl;

    private Long memberId;

    private Long productId;

    private boolean isDeleted;
}
