package com.pragma.jamarlesf.api;

import com.pragma.jamarlesf.api.dto.request.ProductRequest;
import com.pragma.jamarlesf.api.dto.response.ProductResponse;
import com.pragma.jamarlesf.model.branchmodel.BranchModelId;
import com.pragma.jamarlesf.model.productmodel.ProductModel;
import com.pragma.jamarlesf.usecase.addproducttobranch.AddProductToBranchUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ProductHandler {

    private final AddProductToBranchUseCase addProductToBranchUseCase;

    public Mono<ServerResponse> addProduct(ServerRequest request) {
        String branchId = request.pathVariable("branchId");
        return request.bodyToMono(ProductRequest.class)
                .map(req -> ProductModel.builder()
                        .name(req.name())
                        .stock(req.stock())
                        .build())
                .flatMap(product -> addProductToBranchUseCase.execute(new BranchModelId(branchId), product))
                .map(savedProduct -> new ProductResponse(
                        savedProduct.getId() != null ? savedProduct.getId().value() : null,
                        savedProduct.getName(),
                        savedProduct.getStock(),
                        savedProduct.getBranchId() != null ? savedProduct.getBranchId().value() : null
                ))
                .flatMap(response -> ServerResponse
                        .status(HttpStatus.CREATED)
                        .bodyValue(response));
    }
}
