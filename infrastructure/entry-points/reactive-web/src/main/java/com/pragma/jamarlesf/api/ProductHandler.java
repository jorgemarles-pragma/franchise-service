package com.pragma.jamarlesf.api;

import com.pragma.jamarlesf.api.dto.request.ProductRequest;
import com.pragma.jamarlesf.api.dto.request.UpdateProductStockRequest;
import com.pragma.jamarlesf.api.dto.response.ProductResponse;
import com.pragma.jamarlesf.model.branchmodel.BranchModelId;
import com.pragma.jamarlesf.model.franchisemodel.FranchiseModelId;
import com.pragma.jamarlesf.model.productmodel.ProductModel;
import com.pragma.jamarlesf.model.productmodel.ProductModelId;
import com.pragma.jamarlesf.usecase.addproducttobranch.AddProductToBranchUseCase;
import com.pragma.jamarlesf.usecase.gethigheststockproductsbyfranchise.GetHighestStockProductsByFranchiseUseCase;
import com.pragma.jamarlesf.usecase.modifyproductstock.ModifyProductStockUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ProductHandler {

    private final AddProductToBranchUseCase addProductToBranchUseCase;
    private final ModifyProductStockUseCase modifyProductStockUseCase;
    private final GetHighestStockProductsByFranchiseUseCase getHighestStockProductsByFranchiseUseCase;


    public Mono<ServerResponse> addProduct(ServerRequest request) {
        String branchId = request.pathVariable("branchId");
        return request.bodyToMono(ProductRequest.class)
                .map(req -> ProductModel.builder()
                        .name(req.name())
                        .stock(req.stock())
                        .build())
                .flatMap(product -> addProductToBranchUseCase.execute(new BranchModelId(branchId), product))
                .map(savedProduct -> ProductResponse.builder()
                        .id(savedProduct.getId() != null ? savedProduct.getId().value() : null)
                        .name(savedProduct.getName())
                        .stock(savedProduct.getStock())
                        .branchId(savedProduct.getBranchId() != null ? savedProduct.getBranchId().value() : null)
                        .build())
                .flatMap(response -> ServerResponse
                        .status(HttpStatus.CREATED)
                        .bodyValue(response));
    }

    public Mono<ServerResponse> updateStock(ServerRequest request) {
        String productId = request.pathVariable("productId");
        return request.bodyToMono(UpdateProductStockRequest.class)
                .flatMap(req -> modifyProductStockUseCase.execute(new ProductModelId(productId), req.stock()))
                .map(updatedProduct -> ProductResponse.builder()
                        .id(updatedProduct.getId() != null ? updatedProduct.getId().value() : null)
                        .name(updatedProduct.getName())
                        .stock(updatedProduct.getStock())
                        .branchId(updatedProduct.getBranchId() != null ? updatedProduct.getBranchId().value() : null)
                        .build())
                .flatMap(response -> ServerResponse
                        .ok()
                        .bodyValue(response));
    }

    public Mono<ServerResponse> getHighestStockProducts(ServerRequest request) {
        String franchiseId = request.pathVariable("franchiseId");
        return getHighestStockProductsByFranchiseUseCase.execute(new FranchiseModelId(franchiseId))
                .map(product -> ProductResponse.builder()
                        .id(product.getId() != null ? product.getId().value() : null)
                        .name(product.getName())
                        .stock(product.getStock())
                        .branchId(product.getBranchId() != null ? product.getBranchId().value() : null)
                        .build())
                .collectList()
                .flatMap(responses -> ServerResponse
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(responses));
    }
}

