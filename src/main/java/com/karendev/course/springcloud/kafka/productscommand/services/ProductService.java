package com.karendev.course.springcloud.kafka.productscommand.services;

import com.karendev.course.springcloud.kafka.productscommand.models.dto.ProductDto;

import java.util.List;

public interface ProductService {

    ProductDto create(ProductDto dto);

    ProductDto findById(Long id);

    List<ProductDto> findAll();

    ProductDto update(Long id, ProductDto productDto);

    boolean delete(Long id);
}
