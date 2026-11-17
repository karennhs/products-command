package com.karendev.course.springcloud.kafka.productscommand.handlers;

import com.karendev.course.springcloud.kafka.productscommand.models.Command;
import com.karendev.course.springcloud.kafka.productscommand.models.CommandType;
import com.karendev.course.springcloud.kafka.productscommand.models.Reply;
import com.karendev.course.springcloud.kafka.productscommand.models.ReplyStatus;
import com.karendev.course.springcloud.kafka.productscommand.models.dto.ProductDto;
import com.karendev.course.springcloud.kafka.productscommand.services.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.integration.support.MessageBuilder;

import java.util.function.Function;

@Configuration
public class ProductCommandConsumer {
    private static final Logger log = LoggerFactory.getLogger(ProductCommandConsumer.class);


    ProductService service;

    public ProductCommandConsumer(ProductService productService) {
        this.service = productService;
    }


    @Bean
    public Function<Message<Command<ProductDto>>, Message<Reply<Object>>> handleCommands() {
        return msg -> {


            String correlationId = msg.getHeaders().get("correlationId", String.class);
            log.info("Recibiendo correlationId={}", correlationId);

            if (correlationId == null || correlationId.isBlank()) {
                return MessageBuilder.
                        withPayload(new Reply<>(ReplyStatus.ERROR, "Missing correlationId", null))
                        .build();
            }


            Command<ProductDto> cmd = msg.getPayload();
            //String type = cmd.type() == null ? "" : cmd.type().toUpperCase();
            Reply<Object> reply = switch (cmd.type()) {
                case CommandType.CREATE -> {
                    if (cmd.body() == null) {
                        log.warn("Create Empty body");
                        yield new Reply<>(ReplyStatus.ERROR, "Create Empty body", null);
                    } else {
                        ProductDto dto = service.create(cmd.body());
                        log.info("Creating new product id={}, name={}, price={}", dto.id(), dto.name(), dto.price());
                        yield new Reply<>(ReplyStatus.SUCCESS, "Create new name", dto);
                    }
                }
                case CommandType.READ -> {
                    if (cmd.id() == null) {
                        log.warn("Id is required");
                        yield new Reply<>(ReplyStatus.ERROR, "Id is required", null);
                    } else {
                        ProductDto dto = service.findById(cmd.id());
                        log.info("Reading product by id");
                        yield (dto == null) ?
                                new Reply<>(ReplyStatus.ERROR, "Product not found", null) :
                                new Reply<>(ReplyStatus.SUCCESS, "Read product name", dto);
                    }
                }
                case CommandType.READ_ALL -> {
                    log.info("Reading all products");
                    yield new Reply<>(ReplyStatus.SUCCESS, "Read all products", service.findAll());
                }
                case CommandType.UPDATE -> {
                    if (cmd.id() == null || cmd.body() == null) {
                        log.warn("Id or body are required");
                        yield new Reply<>(ReplyStatus.ERROR, "Id or body are required", null);
                    } else {
                        ProductDto dto = service.update(cmd.id(), cmd.body());

                        if (dto != null) {
                            log.info("Updating product id={}, name ={}, price ={}", dto.id(), dto.name(), dto.price());
                            yield new Reply<>(ReplyStatus.SUCCESS, "Update product name", dto);
                        } else {
                            log.info("Product not found, null dto for id={}", cmd.id());
                            yield new Reply<>(ReplyStatus.ERROR, "Product not found", null);
                        }
                    }
                }
                case CommandType.DELETE -> {
                    if (cmd.id() == null) {
                        log.warn("id is required");
                        yield new Reply<>(ReplyStatus.ERROR, "Id is required", null);
                    } else {
                        boolean result = service.delete(cmd.id());
                        log.info("Deleting product");
                        yield result ? new Reply<>(ReplyStatus.SUCCESS, "Deleting Product", "deleted") :
                                new Reply<>(ReplyStatus.ERROR, "Product not found", null);
                    }
                }


                default -> {
                    log.warn("Unknown command type {}", cmd.type());
                    yield new Reply<>(ReplyStatus.ERROR, "Unknown command type", null);

                }
            };

            return MessageBuilder.withPayload(reply)
                    .setHeader("correlationId", correlationId)
                    .build();
        };
    }
}