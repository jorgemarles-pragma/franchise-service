package com.pragma.jamarlesf.usecase.addbranchtofranchise;

import com.pragma.jamarlesf.model.branchmodel.BranchModel;
import com.pragma.jamarlesf.model.branchmodel.gateways.BranchModelRepository;
import com.pragma.jamarlesf.model.exception.FranchiseNotFoundException;
import com.pragma.jamarlesf.model.franchisemodel.FranchiseModelId;
import com.pragma.jamarlesf.model.franchisemodel.gateways.FranchiseModelRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class AddBranchToFranchiseUseCase {

    private final FranchiseModelRepository franchiseModelRepository;
    private final BranchModelRepository branchModelRepository;

    public Mono<BranchModel> execute(FranchiseModelId franchiseId, BranchModel branch) {
        return franchiseModelRepository.findById(franchiseId)
                .switchIfEmpty(Mono.error(new FranchiseNotFoundException(franchiseId.value())))
                .map(franchise -> branch.toBuilder()
                        .franchiseId(franchise.getId())
                        .build())
                .flatMap(branchModelRepository::create);
    }
}
