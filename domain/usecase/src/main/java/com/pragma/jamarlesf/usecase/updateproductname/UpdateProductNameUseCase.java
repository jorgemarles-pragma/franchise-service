package com.pragma.jamarlesf.usecase.updateproductname;

import com.pragma.jamarlesf.model.constant.ErrorMessageConstants;
import com.pragma.jamarlesf.model.exception.InvalidProductNameException;
import com.pragma.jamarlesf.model.exception.ProductNotFoundException;
import com.pragma.jamarlesf.model.productmodel.ProductModel;
import com.pragma.jamarlesf.model.productmodel.ProductModelId;
import com.pragma.jamarlesf.model.productmodel.gateways.ProductModelRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UpdateProductNameUseCase {

    private final ProductModelRepository productModelRepository;

    public Mono<ProductModel> execute(ProductModelId productId, String newName) {
        return Mono.justOrEmpty(newName)
                .map(String::trim)
                .filter(name -> !name.isEmpty())
                .switchIfEmpty(Mono.error(new InvalidProductNameException()))
                .flatMap(validName -> findProductById(productId)
                        .map(existingProduct -> existingProduct.toBuilder()
                                .name(validName)
                                .build()))
                .flatMap(productModelRepository::update);
    }

    private Mono<ProductModel> findProductById(ProductModelId productId) {
        return productModelRepository.findById(productId)
                .switchIfEmpty(Mono.error(new ProductNotFoundException(productId != null ? productId.value() : ErrorMessageConstants.NULL_ID_VALUE)));
    }
}
