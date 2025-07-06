package ksh.emall.product.entity;

import jakarta.persistence.*;
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
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private ProductCategory category;

    private String name;

    private Integer price;

    private DeliveryType deliveryType;

    private Integer quantity;

    private Integer expectedDeliveryDays;

    private String imageUrl;

    @SQLDelete(sql = "update order set is_deleted = true where id = ?")
    @Where(clause = "isDeleted = 0")
    private boolean isDeleted;
}
