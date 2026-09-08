package com.pragma.jamarlesf.usecase.getproductsbybranch;

import com.pragma.jamarlesf.model.branchmodel.BranchModelId;
import com.pragma.jamarlesf.model.branchmodel.gateways.BranchModelRepository;
import com.pragma.jamarlesf.model.constant.ErrorMessageConstants;
import com.pragma.jamarlesf.model.exception.BranchNotFoundException;
import com.pragma.jamarlesf.model.productmodel.ProductModel;
import com.pragma.jamarlesf.model.productmodel.gateways.ProductModelRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class GetProductsByBranchUseCase {

    private final BranchModelRepository branchModelRepository;
    private final ProductModelRepository productModelRepository;

    public Flux<ProductModel> execute(BranchModelId branchId) {
        return branchModelRepository.findById(branchId)
                .switchIfEmpty(Mono.error(new BranchNotFoundException(branchId != null ? branchId.value() : ErrorMessageConstants.NULL_ID_VALUE)))
                .flatMapMany(branch -> productModelRepository.findAllByBranchId(branchId));
    }
}
