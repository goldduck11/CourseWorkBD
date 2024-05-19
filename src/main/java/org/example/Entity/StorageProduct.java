package org.example.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "storage_product")
@IdClass(StorageProductId.class)
public class StorageProduct {
    @Id
    @Column(name = "storage_id")
    private Long storageId;

    @Id
    @Column(name = "product_id")
    private Long productId;

    private Long quantity;
}
