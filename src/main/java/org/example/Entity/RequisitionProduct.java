package org.example.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "requisition_product", schema = "public")
@IdClass(RequisitionProductId.class)
public class RequisitionProduct {
    @Id
    @Column(name = "requisition_id")
    private Long requisitionId;

    @Id
    @Column(name = "product_id")
    private Long productId;
    private Long quantity;
    private Double price;
}
