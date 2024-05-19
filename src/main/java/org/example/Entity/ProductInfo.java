package org.example.Entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.nio.DoubleBuffer;

@Getter
@Setter
@AllArgsConstructor
public class ProductInfo {
    private Long quantity;
    private Double price;
}
