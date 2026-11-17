package com.karendev.course.springcloud.kafka.productscommand.repositories;

import com.karendev.course.springcloud.kafka.productscommand.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
