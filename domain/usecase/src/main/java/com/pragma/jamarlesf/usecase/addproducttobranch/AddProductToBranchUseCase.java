package com.pragma.jamarlesf.usecase.addproducttobranch;

import com.pragma.jamarlesf.model.branchmodel.BranchModelId;
import com.pragma.jamarlesf.model.branchmodel.gateways.BranchModelRepository;
import com.pragma.jamarlesf.model.exception.BranchNotFoundException;
import com.pragma.jamarlesf.model.exception.InvalidProductStockException;
import com.pragma.jamarlesf.model.productmodel.ProductModel;
import com.pragma.jamarlesf.model.productmodel.gateways.ProductModelRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class AddProductToBranchUseCase {

    private final BranchModelRepository branchModelRepository;
    private final ProductModelRepository productModelRepository;

    public Mono<ProductModel> execute(BranchModelId branchId, ProductModel product) {
        return Mono.justOrEmpty(product)
                .filter(p -> p.getStock() != null && p.getStock() >= 0)
                .switchIfEmpty(Mono.error(new InvalidProductStockException("Product stock must be greater than or equal to 0")))
                .flatMap(validProduct -> branchModelRepository.findById(branchId))
                .switchIfEmpty(Mono.error(new BranchNotFoundException(branchId.value())))
                .map(branch -> product.toBuilder()
                        .branchId(branch.getId())
                        .build())
                .flatMap(productModelRepository::create);
    }
}
