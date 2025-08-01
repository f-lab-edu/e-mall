package ksh.emall.product.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import ksh.emall.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@SQLDelete(sql = "update product_review_stat set is_deleted = true where id = ?")
@Where(clause = "is_deleted = false")
public class ProductReviewStat extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double avgScore;

    private Integer totalScore;

    private Integer reviewCount;

    private Long productId;

    private boolean isDeleted;

    public void addReviewScore(int newScore) {
        if(reviewCount == 0){
            this.reviewCount = 0;
            this.totalScore = 0;
            this.avgScore = 0.0;
        }

        this.totalScore += newScore;
        this.reviewCount++;

        this.avgScore = (double) totalScore / reviewCount;
    }
}
