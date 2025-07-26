package ksh.emall.product.entity;

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
@SQLDelete(sql = "update product set is_deleted = true where id = ?")
@Where(clause = "is_deleted = false")
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private ProductCategory category;

    private String name;

    private String brand;

    private Integer price;

    private DeliveryType deliveryType;

    private Integer quantity;

    private Integer expectedDeliveryDays;

    private String imageUrl;

    private boolean isDeleted;

    public LocalDate getGuaranteedDeliveryDate() {
        return LocalDate.now().plusDays(this.expectedDeliveryDays);
    }
}
