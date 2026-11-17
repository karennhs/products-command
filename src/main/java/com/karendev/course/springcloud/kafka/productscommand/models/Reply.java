package com.karendev.course.springcloud.kafka.productscommand.models;

public record Reply<T>(ReplyStatus status, String message, T body) {
}
