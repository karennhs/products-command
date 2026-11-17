package com.karendev.course.springcloud.kafka.productscommand.models.mapper;

import com.karendev.course.springcloud.kafka.productscommand.entities.Product;
import com.karendev.course.springcloud.kafka.productscommand.models.dto.ProductDto;

public final class Mappers {

    private Mappers() {
    }

    static public ProductDto toDto(Product product) {
        return new ProductDto(product.getId(), product.getName(), product.getPrice());
    }

    static public Product toEntity(ProductDto productDto) {
        Product entity = new Product(productDto.name(), productDto.price());
        entity.setId(productDto.id());
        return entity;
    }


}
