package org.example.Entity;

import java.io.Serializable;
import java.util.Objects;

public class StorageProductId implements Serializable {
    private Long storageId;
    private Long productId;

    public StorageProductId() {
    }

    public StorageProductId(Long storageId, Long productId) {
        this.storageId = storageId;
        this.productId = productId;
    }

    // Getters and setters

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StorageProductId that = (StorageProductId) o;
        return storageId == that.storageId && productId == that.productId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(storageId, productId);
    }
}
