package org.example.Entity;

import java.io.Serializable;
import java.util.Objects;

public class RequisitionProductId implements Serializable {
    private Long requisitionId;
    private Long productId;


    public RequisitionProductId() {
    }

    public RequisitionProductId(Long requisitionId, Long productId) {
        this.requisitionId = requisitionId;
        this.productId = productId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RequisitionProductId that = (RequisitionProductId) o;
        return Objects.equals(requisitionId, that.requisitionId) && Objects.equals(productId, that.productId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(requisitionId, productId);
    }
}
