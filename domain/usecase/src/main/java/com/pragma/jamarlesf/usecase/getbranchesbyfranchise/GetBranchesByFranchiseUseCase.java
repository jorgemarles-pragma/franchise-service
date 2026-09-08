package com.pragma.jamarlesf.usecase.getbranchesbyfranchise;

import com.pragma.jamarlesf.model.branchmodel.BranchModel;
import com.pragma.jamarlesf.model.branchmodel.gateways.BranchModelRepository;
import com.pragma.jamarlesf.model.constant.ErrorMessageConstants;
import com.pragma.jamarlesf.model.exception.FranchiseNotFoundException;
import com.pragma.jamarlesf.model.franchisemodel.FranchiseModelId;
import com.pragma.jamarlesf.model.franchisemodel.gateways.FranchiseModelRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class GetBranchesByFranchiseUseCase {

    private final FranchiseModelRepository franchiseModelRepository;
    private final BranchModelRepository branchModelRepository;

    public Flux<BranchModel> execute(FranchiseModelId franchiseId) {
        return franchiseModelRepository.findById(franchiseId)
                .switchIfEmpty(Mono.error(new FranchiseNotFoundException(franchiseId != null ? franchiseId.value() : ErrorMessageConstants.NULL_ID_VALUE)))
                .flatMapMany(franchise -> branchModelRepository.findAllByFranchiseId(franchiseId));
    }
}
