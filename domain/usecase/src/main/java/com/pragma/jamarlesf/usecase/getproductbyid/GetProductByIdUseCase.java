package com.pragma.jamarlesf.usecase.getproductbyid;

import com.pragma.jamarlesf.model.constant.ErrorMessageConstants;
import com.pragma.jamarlesf.model.exception.ProductNotFoundException;
import com.pragma.jamarlesf.model.productmodel.ProductModel;
import com.pragma.jamarlesf.model.productmodel.ProductModelId;
import com.pragma.jamarlesf.model.productmodel.gateways.ProductModelRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class GetProductByIdUseCase {

    private final ProductModelRepository productModelRepository;

    public Mono<ProductModel> execute(ProductModelId id) {
        return productModelRepository.findById(id)
                .switchIfEmpty(Mono.error(new ProductNotFoundException(id != null ? id.value() : ErrorMessageConstants.NULL_ID_VALUE)));
    }
}
