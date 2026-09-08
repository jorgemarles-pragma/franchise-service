package com.pragma.jamarlesf.usecase.modifyproductstock;

import com.pragma.jamarlesf.model.exception.InvalidProductStockException;
import com.pragma.jamarlesf.model.exception.ProductNotFoundException;
import com.pragma.jamarlesf.model.productmodel.ProductModel;
import com.pragma.jamarlesf.model.productmodel.ProductModelId;
import com.pragma.jamarlesf.model.productmodel.gateways.ProductModelRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class ModifyProductStockUseCase {

    private final ProductModelRepository productModelRepository;

    public Mono<ProductModel> execute(ProductModelId productId, Integer newStock) {
        return Mono.justOrEmpty(newStock)
                .filter(stock -> stock >= 0)
                .switchIfEmpty(Mono.error(new InvalidProductStockException("Product stock must be greater than or equal to 0")))
                .flatMap(validStock -> productModelRepository.findById(productId))
                .switchIfEmpty(Mono.error(new ProductNotFoundException(productId != null ? productId.value() : "null")))
                .map(existingProduct -> existingProduct.toBuilder()
                        .stock(newStock)
                        .build())
                .flatMap(productModelRepository::update);
    }
}
