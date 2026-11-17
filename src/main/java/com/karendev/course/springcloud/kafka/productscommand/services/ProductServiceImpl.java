package com.karendev.course.springcloud.kafka.productscommand.services;

import com.karendev.course.springcloud.kafka.productscommand.entities.Product;
import com.karendev.course.springcloud.kafka.productscommand.models.dto.ProductDto;
import com.karendev.course.springcloud.kafka.productscommand.models.mapper.Mappers;
import com.karendev.course.springcloud.kafka.productscommand.repositories.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public ProductDto create(ProductDto dto) {
        return Mappers.toDto(productRepository.save(Mappers.toEntity(dto)));
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDto findById(Long id) {
        return productRepository.findById(id).map(Mappers::toDto).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDto> findAll() {
        return productRepository.findAll()
                .stream()
                .map(Mappers::toDto)
                .toList();
    }

    @Override
    @Transactional
    public ProductDto update(Long id, ProductDto productDto) {

        Optional<Product> entityOptional = productRepository.findById(id);
        if (entityOptional.isPresent()) {
            Product entity = entityOptional.orElse(null);
            entity.setName(productDto.name());
            entity.setPrice(productDto.price());

            return Mappers.toDto(productRepository.save(entity));
        }
        return null;
    }

    @Override
    @Transactional
    public boolean delete(Long id) {
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }

}
