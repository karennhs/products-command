package com.karendev.course.springcloud.kafka.productscommand.models;

public record Command<T>(CommandType type, Long id, T body) {
}
