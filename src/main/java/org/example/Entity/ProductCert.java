package org.example.Entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ProductCert {
    private Product product;
    private ProductInfo productInfo;

    @Override
    public String toString() {
        return product.getName() + ":\n" + " Цена: " + productInfo.getPrice() + "\n Количество: " + productInfo.getQuantity();
    }
}
