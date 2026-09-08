package com.pragma.jamarlesf.usecase.deleteproductfrombranch;

import com.pragma.jamarlesf.model.branchmodel.BranchModelId;
import com.pragma.jamarlesf.model.branchmodel.gateways.BranchModelRepository;
import com.pragma.jamarlesf.model.constant.ErrorMessageConstants;
import com.pragma.jamarlesf.model.exception.BranchNotFoundException;
import com.pragma.jamarlesf.model.exception.ProductNotFoundException;
import com.pragma.jamarlesf.model.productmodel.ProductModelId;
import com.pragma.jamarlesf.model.productmodel.gateways.ProductModelRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class DeleteProductFromBranchUseCase {

    private final BranchModelRepository branchModelRepository;
    private final ProductModelRepository productModelRepository;

    public Mono<Void> execute(BranchModelId branchId, ProductModelId productId) {
        return Mono.justOrEmpty(branchId)
                .flatMap(branchModelRepository::findById)
                .switchIfEmpty(Mono.error(new BranchNotFoundException(branchId != null ? branchId.value() : ErrorMessageConstants.NULL_ID_VALUE)))
                .then(Mono.justOrEmpty(productId))
                .flatMap(productModelRepository::findById)
                .switchIfEmpty(Mono.error(new ProductNotFoundException(productId != null ? productId.value() : ErrorMessageConstants.NULL_ID_VALUE)))
                .filter(product -> product.getBranchId() != null
                        && branchId != null
                        && branchId.value().equals(product.getBranchId().value()))
                .switchIfEmpty(Mono.error(new ProductNotFoundException(ErrorMessageConstants.PRODUCT_NOT_BELONG_TO_BRANCH)))
                .flatMap(product -> productModelRepository.deleteById(product.getId()));
    }
}
